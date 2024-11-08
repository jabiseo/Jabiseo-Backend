package com.jabiseo.api.auth.application.oidc;


import com.jabiseo.api.auth.application.oidc.property.GoogleOidcProperty;
import com.jabiseo.domain.auth.exception.AuthenticationBusinessException;
import com.jabiseo.domain.auth.exception.AuthenticationErrorCode;
import com.jabiseo.domain.member.domain.OauthMemberInfo;
import com.jabiseo.infra.cache.RedisCacheRepository;
import com.jabiseo.infra.client.NetworkApiException;
import com.jabiseo.infra.client.oidc.GoogleAccountsClient;
import com.jabiseo.infra.client.oidc.GoogleOidcClient;
import com.jabiseo.infra.client.oidc.GoogleOpenIdConfiguration;
import com.jabiseo.infra.client.oidc.OidcPublicKey;
import com.jabiseo.domain.common.exception.CommonErrorCode;
import com.jabiseo.domain.member.domain.OauthServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class GoogleIdTokenValidator extends AbstractIdTokenValidator {

    private final RedisCacheRepository redisCacheRepository;
    private final GoogleAccountsClient googleAccountsClient;
    private final GoogleOidcClient googleOidcClient;
    private static final String OPENID_CONFIGURATION_CACHE_KEY = "GOOGLE_OPENID_CONFIGURATION";
    private static final String PUBLIC_KEY_CACHE_KEY = "GOOGLE_OIDC_PUBLIC_KEY";
    private static final String GOOGLE_ID_KEY = "sub";
    private static final String GOOGLE_EMAIL_KEY = "email";

    /*  Google Public Key를 받는 로직
     *  1. https://accounts.google.com/.well-known/openid-configuration -> jwks_uri 값을 파싱해온다.
     *  2. jwks_uri 으로 API를 호출해서 Public Key에 대한 응답 값을 받는다
     *  1번과 2번의 모든 결과는 캐싱을 해야 한다.
     *  Ref: https://developers.google.com/identity/openid-connect/openid-connect?hl=ko#validatinganidtoken
     */

    public GoogleIdTokenValidator(GoogleOidcProperty googleOidcProperty,
                                  IdTokenJwtHandler idTokenJwtHandler,
                                  RedisCacheRepository redisCacheRepository,
                                  GoogleAccountsClient googleAccountsClient,
                                  GoogleOidcClient googleOidcClient) {
        super(googleOidcProperty.toIdTokenProperty(), idTokenJwtHandler);
        this.redisCacheRepository = redisCacheRepository;
        this.googleAccountsClient = googleAccountsClient;
        this.googleOidcClient = googleOidcClient;
    }


    @Override
    protected OidcPublicKey getOidcPublicKey(String kid) {
        List<OidcPublicKey> keys = redisCacheRepository.getPublicKeys(PUBLIC_KEY_CACHE_KEY);

        if (keys == null) {
            keys = getOidcPublicKeysFromGoogle();
            redisCacheRepository.savePublicKey(PUBLIC_KEY_CACHE_KEY, keys);
        }

        return keys.stream().filter((key) -> key.getKid().equals(kid))
                .findAny()
                .orElseThrow(() -> new AuthenticationBusinessException(AuthenticationErrorCode.INVALID_ID_TOKEN));
    }

    private List<OidcPublicKey> getOidcPublicKeysFromGoogle() {
        GoogleOpenIdConfiguration config = redisCacheRepository.getGoogleConfiguration(OPENID_CONFIGURATION_CACHE_KEY);

        try {
            if (config == null) {
                ResponseEntity<GoogleOpenIdConfiguration> oidcConfiguration = googleAccountsClient.getOidcConfiguration();
                config = oidcConfiguration.getBody();
                redisCacheRepository.saveOpenConfiguration(OPENID_CONFIGURATION_CACHE_KEY, config);
            }
            return googleOidcClient.getPublicKeys(config.getJwks_uri());
        } catch (NetworkApiException e) {
            log.error(e.getMessage());
            throw new AuthenticationBusinessException(AuthenticationErrorCode.GET_JWK_FAIL);
        }
    }

    @Override
    protected OauthMemberInfo extractMemberInfoFromPayload(Map<String, Object> payload) {
        String oauthId = (String) payload.get(GOOGLE_ID_KEY);
        String email = (String) payload.get(GOOGLE_EMAIL_KEY);
        if (requireValueIsNull(oauthId, email)) {
            throw new AuthenticationBusinessException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }

        return OauthMemberInfo.builder()
                .oauthId(oauthId)
                .email(email)
                .oauthServer(OauthServer.GOOGLE)
                .build();
    }

    @Override
    OauthServer getOauthServer() {
        return OauthServer.GOOGLE;
    }

    private boolean requireValueIsNull(String oauthId, String email) {
        return oauthId == null || email == null;
    }
}

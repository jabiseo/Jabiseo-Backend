package com.jabiseo.api.auth.application.oidc;

import com.jabiseo.api.auth.application.oidc.property.KakaoOidcProperty;
import com.jabiseo.domain.member.domain.OauthMemberInfo;
import com.jabiseo.infra.cache.RedisCacheRepository;
import com.jabiseo.infra.client.oidc.KakaoKauthClient;
import com.jabiseo.infra.client.NetworkApiException;
import com.jabiseo.infra.client.oidc.OidcPublicKey;
import com.jabiseo.infra.client.oidc.OidcPublicKeyResponse;
import com.jabiseo.domain.auth.exception.AuthenticationBusinessException;
import com.jabiseo.domain.auth.exception.AuthenticationErrorCode;
import com.jabiseo.domain.common.exception.CommonErrorCode;
import com.jabiseo.domain.member.domain.OauthServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class KakaoIdTokenValidator extends AbstractIdTokenValidator {

    private static final String KAKAO_ID_KEY = "sub";
    private static final String KAKAO_EMAIL_KEY = "email";
    private final KakaoKauthClient kakaoKauthClient;
    private final RedisCacheRepository redisCacheRepository;
    private static final String CACHE_KEY = "KAKAO_OIDC_PUBLIC_KEY";

    public KakaoIdTokenValidator(KakaoOidcProperty kakaoOidcProperty, IdTokenJwtHandler idTokenJwtHandler, KakaoKauthClient kakaoKauthClient, RedisCacheRepository redisCacheRepository) {
        super(kakaoOidcProperty.toIdTokenProperty(), idTokenJwtHandler);
        this.kakaoKauthClient = kakaoKauthClient;
        this.redisCacheRepository = redisCacheRepository;
    }

    @Override
    protected OidcPublicKey getOidcPublicKey(String kid) {

        List<OidcPublicKey> keys = redisCacheRepository.getPublicKeys(CACHE_KEY);
        if (keys == null) {
            keys = getOidcPublicKeyByKakaoClient();
            redisCacheRepository.savePublicKey(CACHE_KEY, keys);
        }

        return keys.stream().filter((key) -> key.getKid().equals(kid))
                .findAny()
                .orElseThrow(() -> new AuthenticationBusinessException(AuthenticationErrorCode.INVALID_ID_TOKEN));
    }

    private List<OidcPublicKey> getOidcPublicKeyByKakaoClient() {
        try {
            ResponseEntity<OidcPublicKeyResponse> publicKeys = kakaoKauthClient.getPublicKeys();
            return publicKeys.getBody().getKeys();
        } catch (NetworkApiException e) {
            log.error(e.getMessage());
            throw new AuthenticationBusinessException(AuthenticationErrorCode.GET_JWK_FAIL);
        }
    }

    @Override
    protected OauthMemberInfo extractMemberInfoFromPayload(Map<String, Object> payload) {
        String oauthId = (String) payload.get(KAKAO_ID_KEY);
        String email = (String) payload.get(KAKAO_EMAIL_KEY);
        if (requireValueIsNull(oauthId, email)) {
            throw new AuthenticationBusinessException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }

        return OauthMemberInfo.builder()
                .oauthId(oauthId)
                .oauthServer(OauthServer.KAKAO)
                .email(email)
                .build();
    }

    @Override
    OauthServer getOauthServer() {
        return OauthServer.KAKAO;
    }

    /*
     *  해당 예외가 발생하는건 카카오에서 프로퍼티 key 값을 바꾸지 않는 이상은 발생하지 않는다.
     */
    private boolean requireValueIsNull(String oauthId, String email) {
        return oauthId == null || email == null;
    }
}

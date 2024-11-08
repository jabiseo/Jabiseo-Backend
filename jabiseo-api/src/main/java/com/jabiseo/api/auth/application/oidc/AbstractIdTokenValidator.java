package com.jabiseo.api.auth.application.oidc;


import com.jabiseo.api.auth.application.oidc.property.OidcIdTokenProperty;
import com.jabiseo.domain.member.domain.OauthMemberInfo;
import com.jabiseo.infra.client.oidc.OidcPublicKey;
import com.jabiseo.domain.member.domain.OauthServer;

import java.util.Map;

public abstract class AbstractIdTokenValidator {

    private final OidcIdTokenProperty oidcIdTokenProperty;
    private final IdTokenJwtHandler idTokenJwtHandler;

    public AbstractIdTokenValidator(OidcIdTokenProperty oidcIdTokenProperty, IdTokenJwtHandler idTokenJwtHandler) {
        this.oidcIdTokenProperty = oidcIdTokenProperty;
        this.idTokenJwtHandler = idTokenJwtHandler;
    }

    /*
     * 1. idToken Header 에서 kid라는 key 를 찾고 kid에 맞는 public key를 가져온다.
     * 2. 우리 앱의 issuer, client-id가 jwt token안에 있는지 확인/ signature 에 대한 검증을 진행한다.
     * 3. payload에서 원하는 값을 추출해 return.
     */

    public OauthMemberInfo validate(String idToken) {
        String kid = idTokenJwtHandler.findKidFromHeader(idToken);

        OidcPublicKey oidcPublicKey = getOidcPublicKey(kid);
        Map<String, Object> payload = idTokenJwtHandler.validateAndExtractPayload(idToken, oidcPublicKey, oidcIdTokenProperty.audience(), oidcIdTokenProperty.issuer());

        return extractMemberInfoFromPayload(payload);
    }

    abstract protected OidcPublicKey getOidcPublicKey(String kid);

    abstract protected OauthMemberInfo extractMemberInfoFromPayload(Map<String, Object> payload);

    abstract OauthServer getOauthServer();
}

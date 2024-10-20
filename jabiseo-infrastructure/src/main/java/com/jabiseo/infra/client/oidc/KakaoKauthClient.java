package com.jabiseo.infra.client.oidc;

;
import org.springframework.http.ResponseEntity;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;



@HttpExchange
public interface KakaoKauthClient {

    /*
    * ref: https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#oidc-find-public-key
    * */
    @GetExchange(url = "/.well-known/jwks.json")
    ResponseEntity<OidcPublicKeyResponse> getPublicKeys();

}

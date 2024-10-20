package com.jabiseo.infra.client.oidc;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class GoogleOidcClientImpl implements GoogleOidcClient {

    @Override
    public List<OidcPublicKey> getPublicKeys(String uri) {
        RestClient restClient = RestClient.create();
        OidcPublicKeyResponse response = restClient.get()
                .uri(uri)
                .retrieve()
                .body(OidcPublicKeyResponse.class);
        return response.getKeys();
    }
}

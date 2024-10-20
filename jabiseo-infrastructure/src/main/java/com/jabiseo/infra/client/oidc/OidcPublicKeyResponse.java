package com.jabiseo.infra.client.oidc;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OidcPublicKeyResponse {

    private List<OidcPublicKey> keys;

    public OidcPublicKeyResponse(List<OidcPublicKey> keys) {
        this.keys = keys;
    }
}

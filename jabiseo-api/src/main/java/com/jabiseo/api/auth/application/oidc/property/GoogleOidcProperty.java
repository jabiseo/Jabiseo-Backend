package com.jabiseo.api.auth.application.oidc.property;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "oidc.google")
public class GoogleOidcProperty {

    private final String clientId;
    private final String issuer;

    public GoogleOidcProperty(String clientId, String issuer) {
        this.clientId = clientId;
        this.issuer = issuer;
    }

    public OidcIdTokenProperty toIdTokenProperty() {
        return new OidcIdTokenProperty(issuer, clientId);
    }
}

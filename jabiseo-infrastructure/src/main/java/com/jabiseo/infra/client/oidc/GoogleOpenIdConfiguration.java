package com.jabiseo.infra.client.oidc;


import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@ToString
public class GoogleOpenIdConfiguration {
    private String issuer;
    private String authorization_endpoint;
    private String device_authorization_endpoint;
    private String token_endpoint;
    private String userinfo_endpoint;
    private String revocation_endpoint;
    private String jwks_uri;
    private List<String> response_types_supported;
    private List<String> subject_types_supported;
    private List<String> id_token_signing_alg_values_supported;
    private List<String> scopes_supported;
    private List<String> token_endpoint_auth_methods_supported;
    private List<String> claims_supported;
    private List<String> code_challenge_methods_supported;
    private List<String> grant_types_supported;
}

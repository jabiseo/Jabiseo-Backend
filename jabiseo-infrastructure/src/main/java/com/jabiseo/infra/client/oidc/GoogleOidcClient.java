package com.jabiseo.infra.client.oidc;

import java.util.List;

public interface GoogleOidcClient {

    List<OidcPublicKey> getPublicKeys(String url);

}

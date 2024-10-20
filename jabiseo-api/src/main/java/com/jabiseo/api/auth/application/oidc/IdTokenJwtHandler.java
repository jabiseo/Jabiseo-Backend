package com.jabiseo.api.auth.application.oidc;

import com.jabiseo.infra.client.oidc.OidcPublicKey;
import com.jabiseo.domain.auth.exception.AuthenticationBusinessException;
import com.jabiseo.domain.auth.exception.AuthenticationErrorCode;
import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Component
public class IdTokenJwtHandler {


    public String findKidFromHeader(String jwt) {
        try {
            Jwt<Header, Claims> headerClaimsJwt = Jwts.parserBuilder()
                    .build()
                    .parseClaimsJwt(parseToken(jwt));
            Header header = headerClaimsJwt.getHeader();
            Object kid = header.get("kid");
            if (kid == null) {
                throw new AuthenticationBusinessException(AuthenticationErrorCode.INVALID_ID_TOKEN);
            }

            return (String) kid;
        } catch (ExpiredJwtException e) {
            throw new AuthenticationBusinessException(AuthenticationErrorCode.EXPIRED_ID_TOKEN);
        } catch (Exception e) {
            throw new AuthenticationBusinessException(AuthenticationErrorCode.INVALID_ID_TOKEN);
        }
    }

    public Map<String, Object> validateAndExtractPayload(String idToken, OidcPublicKey publicKey, String aud, String issuer) {
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .requireAudience(aud)
                    .requireIssuer(issuer)
                    .setSigningKey(getRSAPublicKey(publicKey.getN(), publicKey.getE()))
                    .build()
                    .parseClaimsJws(idToken);

            Claims body = claimsJws.getBody();
            return new HashMap<>(body);
        } catch (Exception e) {
            throw new AuthenticationBusinessException(AuthenticationErrorCode.INVALID_ID_TOKEN);
        }
    }

    private String parseToken(String token) {
        String[] splitToken = token.split("\\.");
        if (splitToken.length != 3) {
            throw new AuthenticationBusinessException(AuthenticationErrorCode.INVALID_ID_TOKEN);
        }
        return splitToken[0] + "." + splitToken[1] + ".";
    }

    private Key getRSAPublicKey(String modulus, String exponent)
            throws InvalidKeySpecException, NoSuchAlgorithmException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        byte[] decodeN = Base64.getUrlDecoder()
                .decode(modulus);
        byte[] decodeE = Base64.getUrlDecoder()
                .decode(exponent);
        BigInteger n = new BigInteger(1, decodeN);
        BigInteger e = new BigInteger(1, decodeE);

        RSAPublicKeySpec keySpec = new RSAPublicKeySpec(n, e);
        return keyFactory.generatePublic(keySpec);
    }
}

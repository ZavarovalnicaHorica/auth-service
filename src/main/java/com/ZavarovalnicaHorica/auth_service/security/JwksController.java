package com.ZavarovalnicaHorica.auth_service.security;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Publishes the PUBLIC key in the standard JWKS format.
 * Other services set spring.security.oauth2.resourceserver.jwt.jwk-set-uri to this URL
 * and can then check tokens on their own, without calling auth-service per request.
 */
@RestController
public class JwksController {

    private final JWKSet publicKeys;

    public JwksController(RSAKey rsaKey) {
        this.publicKeys = new JWKSet(rsaKey.toPublicJWK()); // toPublicJWK() drops the private part
    }

    @GetMapping("/.well-known/jwks.json")
    public Map<String, Object> keys() {
        return publicKeys.toJSONObject();
    }
}

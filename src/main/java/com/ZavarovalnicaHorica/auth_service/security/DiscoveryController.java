package com.ZavarovalnicaHorica.auth_service.security;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * OpenID Connect discovery document: tells other services who issues the tokens
 * and where the public keys are. A .NET service only needs
 *   options.Authority = "<issuer>";
 * and finds everything else here.
 *
 * This is the subset needed to CHECK tokens. auth-service is not a full OpenID provider
 * (no /authorize, no ID tokens), so those fields are left out on purpose.
 */
@RestController
public class DiscoveryController {

    private final Map<String, Object> document;

    public DiscoveryController(AuthProperties properties) {
        String issuer = properties.jwt().issuer();
        Map<String, Object> doc = new LinkedHashMap<>();
        doc.put("issuer", issuer);                                   // must equal the "iss" claim in every token
        doc.put("jwks_uri", issuer + "/.well-known/jwks.json");      // where the public keys are
        doc.put("subject_types_supported", List.of("public"));       // "sub" is the same user id for every service
        doc.put("id_token_signing_alg_values_supported", List.of("RS256"));
        doc.put("claims_supported", List.of("iss", "sub", "iat", "exp", "email", "roles"));
        this.document = Collections.unmodifiableMap(doc); // keeps the order above in the JSON
    }

    @GetMapping("/.well-known/openid-configuration")
    public Map<String, Object> configuration() {
        return document;
    }
}

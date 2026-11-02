package com.ZavarovalnicaHorica.auth_service.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.List;

/**
 * Typed view of the "auth:" section in application.yml.
 * auth.jwt.issuer       → jwt().issuer()
 * auth.jwt.access-token-ttl → jwt().accessTokenTtl()
 * auth.cors.allowed-origins → cors().allowedOrigins()
 */
@ConfigurationProperties(prefix = "auth")
public record AuthProperties(Jwt jwt, Cors cors) {

    public record Jwt(String issuer, Duration accessTokenTtl) {
    }

    public record Cors(List<String> allowedOrigins) {
    }
}

package com.ZavarovalnicaHorica.auth_service.security;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

/**
 * The RSA key pair used to sign tokens.
 * Private key: signs tokens (only auth-service has it).
 * Public key:  checks tokens (published at /.well-known/jwks.json for the other services).
 *
 * For now a new key pair is generated on every start, so tokens stop working after a
 * restart. That is fine for development. For Kubernetes (step 9) the key will be loaded
 * from a Secret instead, so all replicas and restarts use the same key.
 */
@Configuration
@EnableConfigurationProperties(AuthProperties.class)
public class JwtKeyConfig {

    @Bean
    public RSAKey rsaKey() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair pair = generator.generateKeyPair();
        return new RSAKey.Builder((RSAPublicKey) pair.getPublic())
                .privateKey((RSAPrivateKey) pair.getPrivate())
                .keyID(UUID.randomUUID().toString()) // "kid": lets services pick the right key later
                .build();
    }

    /** Signs tokens with the private key. */
    @Bean
    public JwtEncoder jwtEncoder(RSAKey rsaKey) {
        return new NimbusJwtEncoder(new ImmutableJWKSet<>(new JWKSet(rsaKey)));
    }

    /** Checks the signature, the expiry time and the issuer of incoming tokens (used by /auth/me). */
    @Bean
    public JwtDecoder jwtDecoder(RSAKey rsaKey, AuthProperties properties) throws Exception {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withPublicKey(rsaKey.toRSAPublicKey()).build();
        decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(properties.jwt().issuer()));
        return decoder;
    }
}

package com.ZavarovalnicaHorica.auth_service.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/** Which URLs are public, which need a token, and how passwords are hashed. */
@Configuration
public class SecurityConfig {

    private static final String[] PUBLIC_URLS = {
            "/auth/register",
            "/auth/login",
            "/.well-known/**",          // public key
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**",          // Swagger's API description
            "/actuator/health/**",      // Kubernetes probes
            "/h2-console/**",           // only enabled in the local profile
            "/error"                    // so error responses are not replaced by 401
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())               // tokens in a header, not cookies → no CSRF risk
                .cors(Customizer.withDefaults())            // uses corsConfigurationSource() below
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(h -> h.frameOptions(f -> f.sameOrigin())) // H2 console runs in a frame
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_URLS).permitAll()
                        .anyRequest().authenticated())
                // Read "Authorization: Bearer <jwt>" and check it with the JwtDecoder bean.
                .oauth2ResourceServer(o -> o.jwt(j -> j.jwtAuthenticationConverter(jwtAuthenticationConverter())));
        return http.build();
    }

    /** BCrypt: slow on purpose and salted, so stolen hashes are hard to crack. */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Turns the token's "roles": ["REVIEWER"] into Spring's ROLE_REVIEWER,
     * so you can later write @PreAuthorize("hasRole('REVIEWER')").
     */
    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter roles = new JwtGrantedAuthoritiesConverter();
        roles.setAuthoritiesClaimName("roles");
        roles.setAuthorityPrefix("ROLE_");
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(roles);
        return converter;
    }

    /**
     * Lets the browser call this API from the web-ui when it runs on another address
     * (Vite dev server on :5173). Behind the Gateway the UI and the API share one address,
     * so no CORS is needed: an empty allowed-origins list turns CORS handling off.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource(AuthProperties properties) {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        List<String> origins = properties.cors() == null ? List.of() : properties.cors().allowedOrigins();
        if (origins == null || origins.isEmpty()) {
            return source; // no configuration registered → requests are not checked for CORS
        }
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(origins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}

package com.ZavarovalnicaHorica.auth_service.auth.dto;

/**
 * Returned by POST /auth/login. The frontend sends accessToken back as
 * "Authorization: Bearer <accessToken>" on every later request.
 */
public record TokenResponse(
        String accessToken,
        String tokenType,
        long expiresIn // seconds
) {
    public static TokenResponse bearer(String token, long expiresInSeconds) {
        return new TokenResponse(token, "Bearer", expiresInSeconds);
    }
}

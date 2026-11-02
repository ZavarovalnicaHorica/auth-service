package com.ZavarovalnicaHorica.auth_service.auth.dto;

import jakarta.validation.constraints.NotBlank;

/** Body of POST /auth/login. */
public record LoginRequest(
        @NotBlank String email,
        @NotBlank String password
) {
}

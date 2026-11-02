package com.ZavarovalnicaHorica.auth_service.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Body of POST /auth/register. The role is NOT here: new users are always CLIENT. */
public record RegisterRequest(
        @NotBlank @Email @Size(max = 255) String email,
        @NotBlank @Size(min = 8, max = 72) String password // BCrypt only uses the first 72 bytes
) {
}

package com.ZavarovalnicaHorica.auth_service.auth.dto;

import com.ZavarovalnicaHorica.auth_service.user.Role;
import com.ZavarovalnicaHorica.auth_service.user.User;

import java.time.Instant;
import java.util.UUID;

/** Public view of a user. Never return the User entity: it contains the password hash. */
public record UserResponse(UUID id, String email, Role role, Instant createdAt) {

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getRole(), user.getCreatedAt());
    }
}

package com.ZavarovalnicaHorica.auth_service.auth;

import com.ZavarovalnicaHorica.auth_service.auth.dto.LoginRequest;
import com.ZavarovalnicaHorica.auth_service.auth.dto.RegisterRequest;
import com.ZavarovalnicaHorica.auth_service.auth.dto.TokenResponse;
import com.ZavarovalnicaHorica.auth_service.auth.dto.UserResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/** HTTP layer only: read the request, call AuthService, return the result. */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    /** Needs a valid token. Spring has already checked it; jwt is the decoded token. */
    @GetMapping("/me")
    @SecurityRequirement(name = "bearerAuth") // shows the lock icon in Swagger
    public UserResponse me(@AuthenticationPrincipal Jwt jwt) {
        return authService.currentUser(UUID.fromString(jwt.getSubject()));
    }
}

package com.ZavarovalnicaHorica.auth_service.auth;

import com.ZavarovalnicaHorica.auth_service.auth.dto.LoginRequest;
import com.ZavarovalnicaHorica.auth_service.auth.dto.RegisterRequest;
import com.ZavarovalnicaHorica.auth_service.auth.dto.TokenResponse;
import com.ZavarovalnicaHorica.auth_service.auth.dto.UserResponse;
import com.ZavarovalnicaHorica.auth_service.security.TokenService;
import com.ZavarovalnicaHorica.auth_service.user.Role;
import com.ZavarovalnicaHorica.auth_service.user.User;
import com.ZavarovalnicaHorica.auth_service.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.UUID;

/** Register / login / current-user logic. The controller only calls this. */
@Service
public class AuthService {

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthService(UserRepository users, PasswordEncoder passwordEncoder, TokenService tokenService) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        String email = normalize(request.email());
        if (users.existsByEmail(email)) {
            throw new EmailAlreadyUsedException(email);
        }
        String hash = passwordEncoder.encode(request.password());
        User saved = users.save(new User(email, hash, Role.CLIENT));
        return UserResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest request) {
        User user = users.findByEmail(normalize(request.email()))
                .orElseThrow(InvalidCredentialsException::new);
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }
        return tokenService.createAccessToken(user);
    }

    @Transactional(readOnly = true)
    public UserResponse currentUser(UUID userId) {
        // Token is valid but the user was deleted meanwhile → treat as not logged in.
        return users.findById(userId)
                .map(UserResponse::from)
                .orElseThrow(InvalidCredentialsException::new);
    }

    /** "Ana@Mail.com " and "ana@mail.com" must be the same account. */
    private static String normalize(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}

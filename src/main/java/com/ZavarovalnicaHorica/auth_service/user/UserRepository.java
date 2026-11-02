package com.ZavarovalnicaHorica.auth_service.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data generates the SQL from the method names:
 * findByEmail → SELECT ... FROM users WHERE email = ?
 */
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}

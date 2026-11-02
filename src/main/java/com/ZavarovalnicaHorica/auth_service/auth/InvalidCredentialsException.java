package com.ZavarovalnicaHorica.auth_service.auth;

/**
 * Thrown on login for a wrong email OR a wrong password. Mapped to 401.
 * Same message in both cases, so nobody can find out which emails are registered.
 */
public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("Invalid email or password");
    }
}

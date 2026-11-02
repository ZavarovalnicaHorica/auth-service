package com.ZavarovalnicaHorica.auth_service.auth;

/** Thrown on register when the email exists. Mapped to 409 Conflict. */
public class EmailAlreadyUsedException extends RuntimeException {

    public EmailAlreadyUsedException(String email) {
        super("Email is already registered: " + email);
    }
}

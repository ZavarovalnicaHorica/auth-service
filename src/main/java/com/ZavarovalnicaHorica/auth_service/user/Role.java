package com.ZavarovalnicaHorica.auth_service.user;

/**
 * What a user is allowed to do. Stored as text in the users.role column
 * and copied into the JWT "roles" claim.
 */
public enum Role {
    CLIENT,    // policy holder: submits claims
    AGENT,     // insurance employee: handles claims
    REVIEWER,  // approves or rejects claims in AWAITING_REVIEW
    ADMIN
}

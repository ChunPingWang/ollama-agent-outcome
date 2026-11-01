package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * Exception thrown when a duplicate email is detected during creation or update.
 */
public class DuplicateEmailException extends RuntimeException {

    private final String email;

    public DuplicateEmailException(String email) {
        super("Duplicate email found: " + email);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}

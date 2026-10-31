package com.github.chunpingwang.ollamaagentoutcome.domain.valueobject;

/**
 * Email value object with validation for format, length, and null checks.
 */
public record Email(String address) {

    private static final int MAX_LENGTH = 254;
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    public Email {
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }
        if (address.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Email exceeds maximum length of " + MAX_LENGTH);
        }
        if (!address.matches(EMAIL_REGEX)) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }
}

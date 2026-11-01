package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * Email address value object with basic validation.
 */
public record Email(String address) {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    public Email {
        if (address == null || address.isBlank() || !address.matches(EMAIL_REGEX)) {
            throw new IllegalArgumentException("invalid email format");
        }
    }
}

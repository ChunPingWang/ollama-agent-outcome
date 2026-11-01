package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * Email address value object with basic format validation.
 */
public record Email(String value) {
    public Email {
        if (value == null || value.isBlank()) {
            throw new InvalidEmailException("email cannot be empty or blank");
        }
        if (!value.contains("@")) {
            throw new InvalidEmailException("invalid email format, missing '@' symbol");
        }
    }
}

package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * Name value object ensuring non-blank and length constraints.
 */
public record Name(String value) {
    private static final int MAX_LENGTH = 50;

    public Name {
        if (value == null) {
            throw new NullPointerException("name must not be null");
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new InvalidNameException("name cannot be blank");
        }
        if (trimmed.length() > MAX_LENGTH) {
            throw new InvalidNameException("name exceeds maximum length of " + MAX_LENGTH);
        }
        value = trimmed;
    }
}

package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * Profile ID value object, wraps a non-empty string identifier.
 */
public record ProfileId(String value) {
    public ProfileId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("ID cannot be empty");
        }
    }
}

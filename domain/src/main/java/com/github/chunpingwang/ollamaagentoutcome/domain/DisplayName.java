package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * Display name value object ensuring non-null and non-blank content.
 */
public record DisplayName(String value) {
    public DisplayName {
        if (value == null) {
            throw new InvalidDisplayNameException("invalid display name: null value");
        }
        if (value.isEmpty()) {
            throw new InvalidDisplayNameException("invalid display name: empty string");
        }
        if (value.isBlank()) {
            throw new InvalidDisplayNameException("invalid display name: only whitespaces");
        }
    }
}

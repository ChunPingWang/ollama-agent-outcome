package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;

/**
 * DisplayName value object allowing null or empty strings but rejecting pure whitespace.
 */
public final class DisplayName {

    private static final String PURE_WHITESPACE_MESSAGE = "Display name cannot be only whitespace";

    private final String value;

    public DisplayName(String value) {
        // Allow null and empty string as per spec ("allows null/empty")
        if (value == null || value.isEmpty()) {
            this.value = value;
            return;
        }

        // Reject pure whitespace if it has content length > 0 but is all spaces/tabs/etc.
        if (value.isBlank()) {
            throw new IllegalArgumentException(PURE_WHITESPACE_MESSAGE);
        }

        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DisplayName that)) return false;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value != null ? value : "";
    }
}

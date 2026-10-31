package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;

/**
 * DisplayName 值物件：允許為 null、空字串，但拒絕純空白。
 */
public final class DisplayName {

    private static final String BLANK_DISPLAY_NAME_MESSAGE = "Display name cannot contain only whitespace.";

    private final String value;

    public DisplayName(String value) {
        // Allow null or empty string, but reject pure whitespace
        if (value != null && !value.isEmpty() && value.isBlank()) {
            throw new IllegalArgumentException(BLANK_DISPLAY_NAME_MESSAGE);
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
}

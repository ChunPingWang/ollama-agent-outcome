package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;

/**
 * Display name value object handling nulls and blanks.
 */
public final class DisplayName {
    private final String name;

    /**
     * Creates a display name, trimming whitespace and treating blank strings as null.
     */
    public DisplayName(String input) {
        if (input == null || input.isBlank()) {
            this.name = null;
        } else {
            this.name = input.trim();
        }
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DisplayName that)) return false;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}

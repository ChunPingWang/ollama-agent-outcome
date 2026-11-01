package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;

/**
 * Display name value object with length limit validation.
 */
public record DisplayName(String name) {

    private static final int MAX_LENGTH = 255;

    public DisplayName {
        if (name != null && name.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("display name length exceeds limit of " + MAX_LENGTH);
        }
    }

    /**
     * Create a display name that allows null or empty as valid state.
     */
    public static DisplayName ofNullable(String name) {
        // If it's just whitespace, treat as null for consistency in business logic if needed, 
        // but strictly following VO immutability and the test requirement which only checks length.
        // The constructor handles length check. Null is allowed by Java type system here implicitly if we don't block it,
        // but let's be explicit about what constitutes a valid instance based on typical usage.
        // However, to keep it simple and match standard VO patterns where null might mean 'unset':
        return new DisplayName(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DisplayName displayName)) return false;
        return Objects.equals(name, displayName.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}

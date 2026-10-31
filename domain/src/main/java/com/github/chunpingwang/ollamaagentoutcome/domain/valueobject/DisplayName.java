package com.github.chunpingwang.ollamaagentoutcome.domain.valueobject;

/**
 * Display Name Value Object with cleaning and length validation. Immutable.
 */
public final class DisplayName {
    private static final int MAX_LENGTH = 50;
    
    private final String name;

    public DisplayName(String name) {
        if (name != null && !name.isBlank()) {
            var trimmed = name.trim();
            if (trimmed.length() > MAX_LENGTH) {
                throw new IllegalArgumentException("Display name length exceeds maximum of " + MAX_LENGTH);
            }
            this.name = trimmed;
        } else {
            this.name = null;
        }
    }

    /**
     * Returns the cleaned display name string.
     */
    public String value() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DisplayName other)) return false;
        return java.util.Objects.equals(name, other.name);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(name);
    }
}

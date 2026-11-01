package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;

/**
 * 顯示名稱 Value Object。
 */
public final class DisplayName {

    private static final int MAX_LENGTH = 255;

    private final String name;

    public DisplayName(String name) {
        // Null or blank is allowed and treated as null/empty
        if (name != null && !name.isBlank()) {
            String trimmed = name.trim();
            if (trimmed.length() > MAX_LENGTH) {
                throw new IllegalArgumentException("Display name too long");
            }
            this.name = trimmed;
        } else {
            this.name = null;
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

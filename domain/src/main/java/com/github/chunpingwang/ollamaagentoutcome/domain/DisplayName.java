package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;

/**
 * Display name value object allowing null or empty values.
 */
public final class DisplayName {
    private final String name;

    public DisplayName(String name) {
        this.name = name != null ? name.trim() : "";
    }

    public String getName() {
        return name;
    }

    public boolean isEmpty() {
        return name.isEmpty();
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

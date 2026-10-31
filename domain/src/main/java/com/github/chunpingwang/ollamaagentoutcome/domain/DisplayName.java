package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;

/**
 * Display name value object handling blanks and trimming.
 */
public final class DisplayName {
    public final String name;

    public DisplayName(String name) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name.trim();
        } else {
            this.name = null;
        }
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

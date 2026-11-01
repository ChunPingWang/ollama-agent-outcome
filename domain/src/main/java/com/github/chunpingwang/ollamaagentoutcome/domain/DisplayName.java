package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;

/**
 * 顯示名稱 Value Object，處理空白與 null。
 */
public final class DisplayName {
    private final String name;

    public DisplayName(String name) {
        if (name != null && !name.isBlank()) {
            this.name = name.trim();
        } else {
            this.name = "";
        }
    }

    public String value() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DisplayName other)) return false;
        return Objects.equals(name, other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
    
    /**
     * Check if the display name is effectively empty.
     */
    public boolean isEmpty() {
        return name.isEmpty();
    }
}

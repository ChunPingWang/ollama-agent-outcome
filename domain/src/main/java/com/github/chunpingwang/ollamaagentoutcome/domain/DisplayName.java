package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;

/**
 * 顯示名稱 Value Object，允許為空或空白（視為 null）。
 */
public final class DisplayName {

    private static final int MAX_LENGTH = 100;

    private final String name;

    public DisplayName(String name) {
        // 若輸入為 null 或僅含空白，則內部儲存為 null
        if (name != null && !name.isBlank()) {
            if (name.length() > MAX_LENGTH) {
                throw new IllegalArgumentException("Display name exceeds max length of " + MAX_LENGTH);
            }
            this.name = name.trim();
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
        return Objects.hashCode(name);
    }

    @Override
    public String toString() {
        return name != null ? name : "";
    }
}

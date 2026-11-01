package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;

/**
 * 顯示名稱值物件。
 * 若輸入為空白字串或僅含空白字符則視為 null。
 */
public final class DisplayName {

    private final String name;

    public DisplayName(String name) {
        if (name == null || name.isBlank()) {
            this.name = null;
        } else {
            this.name = name.trim();
        }
    }

    public String name() {
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

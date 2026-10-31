package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;

/**
 * 顯示名稱 Value Object。
 * 若輸入為 null、空字串或僅含空白字符，內部統一視為空字串處理。
 */
public final class DisplayName {

    private static final String EMPTY = "";

    private final String name;

    public DisplayName(String name) {
        if (name == null || name.isBlank()) {
            this.name = EMPTY;
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

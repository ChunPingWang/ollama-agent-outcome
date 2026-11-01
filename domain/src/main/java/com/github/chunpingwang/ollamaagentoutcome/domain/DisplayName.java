package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;

/**
 * 顯示名稱值物件。
 * 若輸入為空白或 null，內部狀態視為 null。
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

    /**
     * 取得顯示名稱。
     * @return 顯示名稱，可能為 null
     */
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

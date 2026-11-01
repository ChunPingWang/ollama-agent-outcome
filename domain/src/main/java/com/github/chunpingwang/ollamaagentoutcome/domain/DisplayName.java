package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;

/**
 * 顯示名稱值物件，null 或空白會轉換為空字串。
 */
public final class DisplayName {
    private final String value;

    public DisplayName(String value) {
        // 允許 null 輸入，統一轉為 ""
        this.value = (value != null && !value.isBlank()) ? value : "";
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DisplayName that)) return false;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
    
    @Override
    public String toString() {
        return "DisplayName{" +
                "value='" + value + '\'' +
                '}';
    }
}

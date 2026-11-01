package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;

/**
 * 顯示名稱 Value Object，處理空白與 null 情況。
 */
public final class DisplayName {
    
    private final String name;

    /**
     * 建立新的 DisplayName 實例。若為空則設為空字串。
     */
    public DisplayName(String name) {
        if (name != null && !name.isBlank()) {
            this.name = name.trim();
        } else {
            this.name = "";
        }
    }

    /**
     * 靜態工廠方法建立新的 DisplayName 實例。
     */
    public static DisplayName of(String rawName) {
        return new DisplayName(rawName);
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

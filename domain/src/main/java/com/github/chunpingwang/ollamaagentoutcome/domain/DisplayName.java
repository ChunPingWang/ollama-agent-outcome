package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;

/**
 * 顯示名稱值物件，包含長度與非空驗證。
 */
public final class DisplayName {

    private static final int MAX_LENGTH = 255;

    private final String name;

    public DisplayName(String name) {
        if (name == null) {
            throw new NullPointerException("顯示名稱不能為空");
        }
        if (name.isBlank()) {
            throw new IllegalArgumentException("顯示名稱不能為空白");
        }
        if (name.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("長度不得超過 255");
        }
        this.name = name;
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

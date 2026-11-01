package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;

/**
 * 顯示名稱值物件，允許為空或空白，但有長度限制。
 */
public final class DisplayName {

    private static final int MAX_LENGTH = 100;

    private final String value;

    public DisplayName(String value) {
        // Allow null or empty string as per spec requirements for optional display names
        this.value = value; 
        
        if (value != null && value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("display name length must not exceed " + MAX_LENGTH);
        }
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
}

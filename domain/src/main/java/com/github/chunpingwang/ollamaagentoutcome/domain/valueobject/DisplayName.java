package com.github.chunpingwang.ollamaagentoutcome.domain.valueobject;

import java.util.Objects;

/**
 * DisplayName 值物件：驗證顯示名稱長度。
 */
public final class DisplayName {

    private static final int MIN_LENGTH = 1;
    private static final int MAX_LENGTH = 100;

    private final String value;

    public DisplayName(String value) {
        if (value == null || value.length() < MIN_LENGTH || value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                "Invalid display name length: must be between " + MIN_LENGTH + " and " + MAX_LENGTH + ", got " + 
                (value != null ? value.length() : "null"));
        }
        this.value = value;
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

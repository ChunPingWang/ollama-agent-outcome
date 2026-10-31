package com.github.chunpingwang.ollamaagentoutcome.domain.valueobject;

import java.util.Objects;

/**
 * Bio 值物件：驗證個人簡介長度。
 */
public final class Bio {

    private static final int MAX_LENGTH = 500;

    private final String value;

    public Bio(String value) {
        if (value != null && value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Bio length exceeds maximum of " + MAX_LENGTH);
        }
        this.value = value;
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Bio bio)) return false;
        return Objects.equals(value, bio.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
    
    @Override
    public String toString() {
        return value;
    }
}

package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;
import java.util.UUID;

/**
 * Profile 的唯一識別碼 Value Object。
 */
public final class ProfileId {

    private static final String EMPTY = "00000000-0000-0000-0000-000000000000";

    private final UUID value;

    public ProfileId(UUID value) {
        Objects.requireNonNull(value, "ID cannot be null");
        if (value.toString().equals(EMPTY)) {
            throw new IllegalArgumentException("Invalid empty ID");
        }
        this.value = value;
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProfileId that)) return false;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value.toString();
    }
}

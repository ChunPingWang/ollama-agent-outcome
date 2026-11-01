package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;
import java.util.UUID;

/**
 * 個人資料的唯一識別碼 Value Object。
 */
public final class ProfileId {

    private static final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

    private final String value;

    public ProfileId(String value) {
        if (value == null || !value.matches(UUID_REGEX)) {
            throw new IllegalArgumentException("Invalid profile ID format");
        }
        this.value = value.toLowerCase();
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProfileId that)) return false;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}

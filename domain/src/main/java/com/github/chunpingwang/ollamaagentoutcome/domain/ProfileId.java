package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;
import java.util.UUID;

/**
 * 個人資料識別碼值物件。
 * 確保識別碼的不可變性與類型安全。
 */
public final class ProfileId {

    private final UUID value;

    public ProfileId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("UUID cannot be null");
        }
        this.value = value;
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProfileId profileId = (ProfileId) o;
        return Objects.equals(value, profileId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}

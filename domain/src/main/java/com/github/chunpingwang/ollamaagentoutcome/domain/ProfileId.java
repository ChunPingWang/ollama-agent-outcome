package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;
import java.util.UUID;

/**
 * 個人資料識別碼 Value Object。
 * 驗證值必須為有效的 UUID 格式字串。
 */
public final class ProfileId {

    private static final String ID_PATTERN = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

    private final String value;

    public ProfileId(String value) {
        if (value == null || !value.matches(ID_PATTERN)) {
            throw new IllegalArgumentException("invalid uuid format for profile id");
        }
        this.value = value.toLowerCase();
    }

    public String value() {
        return value;
    }

    /**
     * 產生新的隨機 UUID 識別碼。
     */
    public static ProfileId generateRandom() {
        return new ProfileId(UUID.randomUUID().toString());
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

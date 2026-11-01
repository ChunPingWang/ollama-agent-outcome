package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.UUID;

/**
 * 個人資料識別碼值物件，封裝 UUID 字串並驗證格式。
 */
public final class ProfileId {

    private static final String ID_PATTERN = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

    private final String value;

    public ProfileId(String value) {
        if (value == null || !value.matches(ID_PATTERN)) {
            throw new IllegalArgumentException("invalid profile id format");
        }
        this.value = value.toLowerCase();
    }

    public String value() {
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
}

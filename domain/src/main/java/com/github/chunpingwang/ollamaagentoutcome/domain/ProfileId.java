package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.UUID;

/**
 * 個人資料識別碼值物件。
 * 驗證 UUID 格式並確保不可變性。
 */
public final class ProfileId {

    private final String value;

    public ProfileId(String value) {
        try {
            // 嘗試解析以驗證格式，若無效則拋出 IllegalArgumentException
            UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("invalid uuid format: " + value);
        }
        this.value = value.toLowerCase();
    }

    /**
     * 取得識別碼字串。
     * @return UUID 字串
     */
    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProfileId that)) return false;
        return java.util.Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(value);
    }
}

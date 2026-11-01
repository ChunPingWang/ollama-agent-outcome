package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;
import java.util.UUID;

/**
 * 個人資料識別碼值物件。
 * 驗證 UUID 格式並儲存標準化字串。
 */
public final class ProfileId {

    private static final String FIELD_NAME = "value";
    private final String value;

    public ProfileId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("格式無效：識別碼不能為空");
        }
        try {
            // 驗證並標準化為 UUID.toString() 的格式（小寫，帶連字號）
            this.value = UUID.fromString(value).toString();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("格式無效：" + value);
        }
    }

    /**
     * 取得識別碼字串。
     * @return UUID 字串
     */
    public String value() {
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

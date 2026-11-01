package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;
import java.util.UUID;

/**
 * 個人資料識別碼 Value Object。封裝 UUID 以確保類型安全。
 */
public final class ProfileId {

    private static final String VALIDATION_MESSAGE = "無效的 UUID 格式";

    private final String uuidString;

    public ProfileId(String uuidString) {
        Objects.requireNonNull(uuidString, "UUID 字串不能為 null");
        if (uuidString.isBlank()) {
            throw new IllegalArgumentException("UUID 字串不能為空白");
        }
        try {
            // 嘗試解析為標準 UUID，若失敗則拋出異常
            UUID.fromString(uuidString);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(VALIDATION_MESSAGE, e);
        }
        this.uuidString = uuidString;
    }

    /**
     * 取得 UUID 物件。
     */
    public UUID toUuid() {
        return UUID.fromString(uuidString);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProfileId that)) return false;
        return uuidString.equals(that.uuidString);
    }

    @Override
    public int hashCode() {
        return uuidString.hashCode();
    }

    @Override
    public String toString() {
        return uuidString;
    }
}

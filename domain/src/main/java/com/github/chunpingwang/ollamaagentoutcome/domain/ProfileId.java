package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.UUID;

/**
 * 個人資料識別碼值物件。
 * 驗證 UUID 格式並確保不可變性。
 */
public record ProfileId(String value) {

    public ProfileId {
        if (value == null || !isValidUuid(value)) {
            throw new IllegalArgumentException("invalid uuid format");
        }
    }

    private static boolean isValidUuid(String uuid) {
        try {
            UUID.fromString(uuid);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}

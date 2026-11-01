package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.UUID;

/**
 * 個人資料識別碼 Value Object。
 */
public record ProfileId(String value) {
    public ProfileId {
        if (value == null || !isValidUuid(value)) {
            throw new IllegalArgumentException("invalid UUID format");
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

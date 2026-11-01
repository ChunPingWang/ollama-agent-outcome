package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;
import java.util.UUID;

/**
 * 個人資料識別碼值物件。
 * 驗證輸入字串是否為有效的 UUID 格式。
 */
public record ProfileId(String value) {

    public ProfileId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("id cannot be blank");
        }
        try {
            UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("invalid format for profile id: " + value);
        }
    }
}

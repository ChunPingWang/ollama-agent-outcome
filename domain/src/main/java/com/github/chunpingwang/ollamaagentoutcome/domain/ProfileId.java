package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.UUID;

/**
 * 個人資料的唯一識別碼值物件。
 */
public record ProfileId(UUID value) {
    public ProfileId {
        if (value == null) {
            throw new IllegalArgumentException("UUID cannot be null");
        }
    }

    /**
     * 取得識別碼值。
     */
    public UUID getValue() {
        return value;
    }
}

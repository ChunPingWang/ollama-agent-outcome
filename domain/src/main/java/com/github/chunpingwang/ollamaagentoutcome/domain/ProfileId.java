package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.UUID;

/**
 * 個人資料的唯一識別碼值物件。
 */
public record ProfileId(UUID value) {

    /**
     * 使用指定的 UUID 建立識別碼。
     *
     * @param value UUID 值
     */
    public ProfileId {
        if (value == null) {
            throw new IllegalArgumentException("UUID must not be null");
        }
    }

    /**
     * 建立新的識別碼，自動產生 UUID。
     *
     * @return 新的 ProfileId 實例
     */
    public static ProfileId generate() {
        return new ProfileId(UUID.randomUUID());
    }
}

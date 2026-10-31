package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.UUID;

/**
 * 個人資料的唯一識別碼 Value Object。
 */
public record ProfileId(UUID id) {

    /**
     * 建立 ProfileId。
     *
     * @param id UUID，不可為 null
     * @throws IllegalArgumentException 如果 id 為 null
     */
    public ProfileId {
        if (id == null) {
            throw new IllegalArgumentException("UUID cannot be null");
        }
    }
}

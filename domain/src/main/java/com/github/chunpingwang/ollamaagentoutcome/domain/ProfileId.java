package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.UUID;

/**
 * 個人資料的唯一識別碼 Value Object。
 */
public record ProfileId(UUID id) {
    public ProfileId {
        if (id == null) {
            throw new IllegalArgumentException("ProfileId cannot be null");
        }
    }
}

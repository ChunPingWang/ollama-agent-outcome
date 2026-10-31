package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.UUID;

/**
 * 個人資料的唯一識別碼值物件。
 */
public record ProfileId(UUID value) {
    public ProfileId {
        if (value == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
    }
}

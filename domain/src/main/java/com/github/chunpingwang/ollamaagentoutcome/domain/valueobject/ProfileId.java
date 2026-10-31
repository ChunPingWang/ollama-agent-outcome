package com.github.chunpingwang.ollamaagentoutcome.domain.valueobject;

import java.util.UUID;

/**
 * 個人資料的唯一識別碼值物件。
 */
public record ProfileId(UUID value) {
    public ProfileId {
        if (value == null) {
            throw new IllegalArgumentException("Profile ID must not be null");
        }
    }
}

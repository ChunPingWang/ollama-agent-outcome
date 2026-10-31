package com.github.chunpingwang.ollamaagentoutcome.domain.valueobject;

import java.util.UUID;

/**
 * Profile 的唯一識別碼值對象。
 */
public record ProfileId(UUID value) {
    public ProfileId {
        if (value == null) {
            throw new IllegalArgumentException("Profile ID cannot be null");
        }
    }
}

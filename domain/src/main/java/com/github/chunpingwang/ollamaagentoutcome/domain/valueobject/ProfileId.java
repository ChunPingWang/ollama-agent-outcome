package com.github.chunpingwang.ollamaagentoutcome.domain.valueobject;

import java.util.UUID;

/**
 * Profile 的唯一識別碼 Value Object。
 */
public record ProfileId(UUID value) {
    public ProfileId {
        if (value == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
    }
}

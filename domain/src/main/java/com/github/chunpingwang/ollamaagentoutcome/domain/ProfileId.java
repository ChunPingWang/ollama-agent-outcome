package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.UUID;

/**
 * Profile 的唯一識別碼值物件。
 */
public record ProfileId(UUID id) {
    public ProfileId {
        if (id == null) {
            throw new IllegalArgumentException("ProfileId cannot be null");
        }
    }
}

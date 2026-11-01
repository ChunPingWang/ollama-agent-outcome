package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;
import java.util.UUID;

/**
 * 個人資料的唯一識別碼值物件。
 */
public record ProfileId(UUID id) {
    public ProfileId {
        Objects.requireNonNull(id, "id cannot be null");
    }
}

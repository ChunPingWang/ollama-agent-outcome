package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;
import java.util.UUID;

/**
 * ProfileId 值物件：代表個人資料的唯一識別碼。
 */
public final class ProfileId {

    private static final String NULL_ID_MESSAGE = "ID cannot be null";

    private final UUID id;

    public ProfileId(UUID id) {
        this.id = Objects.requireNonNull(id, NULL_ID_MESSAGE);
    }

    public UUID getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProfileId that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

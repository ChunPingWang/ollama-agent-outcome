package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;
import java.util.UUID;

/**
 * Profile 的唯一識別碼值物件。
 */
public final class ProfileId {
    private final UUID id;

    public ProfileId(UUID id) {
        this.id = Objects.requireNonNull(id, "id must not be null");
    }

    public UUID value() {
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

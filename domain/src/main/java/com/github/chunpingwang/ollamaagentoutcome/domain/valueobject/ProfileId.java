package com.github.chunpingwang.ollamaagentoutcome.domain.valueobject;

import java.util.Objects;
import java.util.UUID;

/**
 * ProfileId 值物件：個人資料唯一識別碼。
 */
public final class ProfileId {

    private final UUID id;

    public ProfileId(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        this.id = id;
    }

    public static ProfileId random() {
        return new ProfileId(UUID.randomUUID());
    }

    public UUID uuid() {
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
    
    @Override
    public String toString() {
        return id.toString();
    }
}

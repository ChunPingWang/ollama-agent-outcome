package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;
import java.util.UUID;

/**
 * ProfileId value object representing a unique identifier for a profile.
 */
public final class ProfileId {

    private static final String NULL_ID_MESSAGE = "Profile ID cannot be null";

    private final UUID id;

    public ProfileId(UUID id) {
        Objects.requireNonNull(id, NULL_ID_MESSAGE);
        this.id = id;
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

    @Override
    public String toString() {
        return id.toString();
    }
}

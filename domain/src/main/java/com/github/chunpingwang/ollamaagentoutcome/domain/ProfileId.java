package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.UUID;

/**
 * Profile ID value object wrapping a UUID.
 */
public record ProfileId(UUID value) {
    public ProfileId {
        if (value == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
    }
}

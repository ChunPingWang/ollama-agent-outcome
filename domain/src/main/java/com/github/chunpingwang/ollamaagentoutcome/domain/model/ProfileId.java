package com.github.chunpingwang.ollamaagentoutcome.domain.model;

import java.util.UUID;

/**
 * Profile ID value object.
 */
public record ProfileId(UUID value) {
    public ProfileId {
        if (value == null) {
            throw new IllegalArgumentException("id must not be null");
        }
    }
}

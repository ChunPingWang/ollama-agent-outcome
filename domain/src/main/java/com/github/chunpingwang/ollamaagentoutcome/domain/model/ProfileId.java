package com.github.chunpingwang.ollamaagentoutcome.domain.model;

import java.util.UUID;

/**
 * ProfileId represents the unique identifier for a profile entity.
 */
public record ProfileId(UUID value) {

    public ProfileId {
        if (value == null) {
            throw new IllegalArgumentException("UUID cannot be null");
        }
    }
}

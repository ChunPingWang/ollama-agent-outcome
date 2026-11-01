package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.UUID;

/**
 * Profile ID value object wrapping a UUID.
 */
public record ProfileId(UUID id) {
    public ProfileId {
        if (id == null) {
            throw new NullPointerException("profile id must not be null");
        }
    }
}

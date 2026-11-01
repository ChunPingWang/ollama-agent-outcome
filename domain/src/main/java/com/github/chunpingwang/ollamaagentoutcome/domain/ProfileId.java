package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.UUID;

/**
 * Profile identifier value object ensuring valid UUID format.
 */
public record ProfileId(String value) {
    public ProfileId {
        if (value == null || !isValidUuid(value)) {
            throw new IllegalArgumentException("invalid uuid format");
        }
    }

    private static boolean isValidUuid(String uuid) {
        try {
            UUID.fromString(uuid);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}

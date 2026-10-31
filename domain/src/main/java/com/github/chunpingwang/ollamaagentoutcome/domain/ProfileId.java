package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.UUID;

/**
 * Profile identifier value object wrapping a UUID string.
 */
public final class ProfileId {
    private static final String VALID_UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

    public final String value;

    public ProfileId(String value) {
        if (value == null || !value.matches(VALID_UUID_REGEX)) {
            throw new IllegalArgumentException("Invalid profile ID format");
        }
        this.value = value.toLowerCase();
    }
}

package com.github.chunpingwang.ollamaagentoutcome.domain.profile;

import java.util.UUID;

/**
 * Profile ID Value Object，封裝 UUID 以提供領域語意。
 */
public record ProfileId(UUID value) {

    public static final String PATTERN_UUID = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$";

    public ProfileId {
        if (value == null || value.toString().isBlank()) {
            throw new IllegalArgumentException("ID cannot be null or blank");
        }
    }

    /**
     * 建立新的 ProfileId（靜態工廠）。
     */
    public static ProfileId of(String rawValue) {
        try {
            return new ProfileId(UUID.fromString(rawValue));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UUID format: " + rawValue, e);
        }
    }
}

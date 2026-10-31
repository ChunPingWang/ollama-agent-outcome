package com.github.chunpingwang.ollamaagentoutcome.domain.valueobject;

/**
 * DisplayName value object allowing null or empty strings but rejecting excessive whitespace-only content.
 */
public record DisplayName(String name) {

    private static final int MAX_LENGTH = 100;

    /**
     * Explicit canonical constructor handles input normalization before storage.
     */
    public DisplayName(String name) {
        this(normalize(name));
    }

    private static String normalize(String input) {
        if (input == null || input.isBlank()) {
            return null;
        }
        String trimmed = input.trim();
        if (trimmed.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Display name exceeds maximum length of " + MAX_LENGTH);
        }
        return trimmed;
    }
}

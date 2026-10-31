package com.github.chunpingwang.ollamaagentoutcome.domain.valueobject;

/**
 * Bio value object representing a short introduction text with length limit.
 */
public record Bio(String text) {

    private static final int MAX_LENGTH = 500;

    public Bio {
        if (text != null && text.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Bio exceeds maximum length of " + MAX_LENGTH);
        }
    }
}

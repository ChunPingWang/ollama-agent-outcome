package com.github.chunpingwang.ollamaagentoutcome.domain.model;

/**
 * Bio content value object with length limit.
 */
public record Bio(String content) {
    private static final int MAX_LENGTH = 500;

    public Bio {
        if (content != null && content.length() > MAX_LENGTH) {
            throw new BioTooLongException("bio must not exceed " + MAX_LENGTH + " characters");
        }
    }
}

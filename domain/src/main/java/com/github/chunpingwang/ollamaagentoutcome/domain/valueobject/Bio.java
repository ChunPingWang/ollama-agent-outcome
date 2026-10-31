package com.github.chunpingwang.ollamaagentoutcome.domain.valueobject;

import com.github.chunpingwang.ollamaagentoutcome.domain.exception.BioTooLongException;

/**
 * Bio 值對象，限制最大長度。
 */
public record Bio(String content) {
    private static final int MAX_LENGTH = 500;

    public Bio {
        if (content != null && content.length() > MAX_LENGTH) {
            throw new BioTooLongException("Bio must not exceed " + MAX_LENGTH + " characters");
        }
    }
}

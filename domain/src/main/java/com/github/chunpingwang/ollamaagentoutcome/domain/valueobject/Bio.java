package com.github.chunpingwang.ollamaagentoutcome.domain.valueobject;

import com.github.chunpingwang.ollamaagentoutcome.domain.exception.BioTooLongException;

/**
 * 個人簡介值物件，限制最大長度以防止過長數據存入。
 */
public record Bio(String content) {
    private static final int MAX_LENGTH = 500;

    public Bio {
        if (content != null && content.length() > MAX_LENGTH) {
            throw new BioTooLongException(MAX_LENGTH, content);
        }
    }
}

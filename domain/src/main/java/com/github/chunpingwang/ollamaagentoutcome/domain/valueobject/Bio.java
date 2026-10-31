package com.github.chunpingwang.ollamaagentoutcome.domain.valueobject;

/**
 * 個人簡介值物件，若存在則長度需 <= 500 字元。
 */
public record Bio(String value) {
    private static final int MAX_LENGTH = 500;

    public Bio {
        if (value != null && value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Bio length exceeds maximum limit of " + MAX_LENGTH);
        }
    }
}

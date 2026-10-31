package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 使用者年齡值物件。
 */
public record Age(int value) {
    public Age {
        if (value <= 0) {
            throw new InvalidAgeException("Age must be greater than 0");
        }
    }

    /**
     * 取得年齡值。
     */
    public int getValue() {
        return value;
    }
}

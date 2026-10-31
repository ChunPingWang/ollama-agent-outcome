package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 年齡 Value Object，包含正整數驗證。
 */
public record Age(int value) {

    public Age {
        if (value <= 0) {
            throw new InvalidAgeException("Age must be a positive integer");
        }
    }

    /**
     * 取得年齡值。
     *
     * @return 年齡整數
     */
    public int getValue() {
        return value;
    }
}

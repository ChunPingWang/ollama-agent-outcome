package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 用戶姓名值物件。
 */
public record Name(String value) {
    public Name {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Name cannot be blank");
        }
        if (value.length() > 100) {
            throw new IllegalArgumentException("Name must not exceed 100 characters");
        }
    }
}

package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 用戶姓名 Value Object。
 */
public record Name(String value) {
    public Name {
        if (value == null) {
            throw new InvalidInputException("name cannot be null");
        }
        if (value.isBlank()) {
            throw new InvalidInputException("name must not be blank");
        }
    }
}

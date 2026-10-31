package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 電子郵件地址 Value Object。
 */
public record Email(String value) {
    public Email {
        if (value == null) {
            throw new InvalidInputException("email cannot be null");
        }
        if (value.isBlank()) {
            throw new InvalidInputException("email must not be blank");
        }
    }
}

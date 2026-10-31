package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 電子郵件地址 Value Object。
 */
public record Email(String value) {
    public Email {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
    }
}

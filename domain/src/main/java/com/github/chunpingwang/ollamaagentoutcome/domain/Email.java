package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 電子郵件地址值物件，包含基本格式驗證。
 */
public record Email(String value) {
    public Email {
        if (value == null || !value.contains("@")) {
            throw new InvalidEmailFormatException("Invalid email format: " + value);
        }
    }
}

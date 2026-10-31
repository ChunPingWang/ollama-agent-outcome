package com.github.chunpingwang.ollamaagentoutcome.domain.valueobject;

import com.github.chunpingwang.ollamaagentoutcome.domain.exception.InvalidEmailException;

/**
 * 電子郵件地址值物件，包含格式驗證邏輯。
 */
public record Email(String value) {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    public Email {
        if (value == null || value.isBlank()) {
            throw new InvalidEmailException("Invalid email address: cannot be empty or blank");
        }
        if (!value.matches(EMAIL_REGEX)) {
            throw new InvalidEmailException("Invalid email format: " + value);
        }
    }
}

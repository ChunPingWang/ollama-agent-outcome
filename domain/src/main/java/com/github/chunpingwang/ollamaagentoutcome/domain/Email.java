package com.github.chunpingwang.ollamaagentoutcome.domain;

import com.github.chunpingwang.ollamaagentoutcome.domain.exception.InvalidEmailException;

/**
 * 電子郵件地址 Value Object，確保格式正確性。
 */
public record Email(String value) {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private static final int MAX_LENGTH = 254;

    public Email {
        if (value == null) {
            throw new InvalidEmailException("Email cannot be null");
        }
        if (value.isBlank()) {
            throw new InvalidEmailException("Email cannot be blank");
        }
        if (value.length() > MAX_LENGTH) {
            throw new InvalidEmailException("Email is too long");
        }
        if (!value.matches(EMAIL_REGEX)) {
            throw new InvalidEmailException("Invalid email format");
        }
    }
}

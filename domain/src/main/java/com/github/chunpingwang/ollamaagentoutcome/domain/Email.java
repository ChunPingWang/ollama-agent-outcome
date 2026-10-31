package com.github.chunpingwang.ollamaagentoutcome.domain;

import com.github.chunpingwang.ollamaagentoutcome.domain.exception.InvalidEmailException;

/**
 * 電子郵件地址 Value Object，確保格式正確性與不可變性。
 */
public record Email(String value) {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private static final int MAX_LENGTH = 254;

    /**
     * 建立 Email。
     *
     * @param value 電子郵件字串
     * @throws IllegalArgumentException 如果 value 為 null 或空白
     * @throws InvalidEmailException 如果格式無效或長度過長
     */
    public Email {
        if (value == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }
        if (value.isBlank()) {
            throw new IllegalArgumentException("Email cannot be blank");
        }
        if (value.length() > MAX_LENGTH) {
            throw new InvalidEmailException("Email length must not exceed 254 characters");
        }
        if (!value.matches(EMAIL_REGEX)) {
            throw new InvalidEmailException("Invalid email format");
        }
    }
}

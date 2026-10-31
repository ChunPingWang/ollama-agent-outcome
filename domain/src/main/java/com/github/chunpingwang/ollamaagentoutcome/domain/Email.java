package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 電子郵件值物件。
 */
public record Email(String value) {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    public Email {
        if (value == null) {
            throw new IllegalArgumentException("電子郵件不可為 null");
        }
        if (!value.matches(EMAIL_REGEX)) {
            throw new IllegalArgumentException("電子郵件格式無效");
        }
    }
}

package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * Email 值物件，驗證 RFC 5322 基本格式且非空白。
 */
public record Email(String value) {
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    public Email {
        if (value == null || value.isBlank()) {
            throw new InvalidEmailException("Email must not be blank");
        }
        if (!value.matches(EMAIL_REGEX)) {
            throw new InvalidEmailException("Invalid email format: " + value);
        }
    }
}

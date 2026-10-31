package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 電子郵件 Value Object，包含格式驗證。
 */
public record Email(String value) {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    public Email {
        if (value == null) {
            throw new InvalidEmailException("Email cannot be null");
        }
        if (value.isBlank()) {
            throw new InvalidEmailException("Email cannot be blank");
        }
        if (!value.matches(EMAIL_REGEX)) {
            throw new InvalidEmailException("Invalid email format");
        }
    }

    /**
     * 取得電子郵件字串。
     *
     * @return 電子郵件字串
     */
    public String getValue() {
        return value;
    }
}

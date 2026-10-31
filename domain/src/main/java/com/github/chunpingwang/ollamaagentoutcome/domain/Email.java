package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 電子郵件地址 Value Object，需符合基本格式規範。
 */
public record Email(String address) {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    public Email {
        if (address == null || address.isBlank()) {
            throw new InvalidProfileException("email format is invalid");
        }
        if (!address.matches(EMAIL_REGEX)) {
            throw new InvalidProfileException("email format is invalid");
        }
    }
}

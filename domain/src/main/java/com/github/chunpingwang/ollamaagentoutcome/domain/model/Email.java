package com.github.chunpingwang.ollamaagentoutcome.domain.model;

import com.github.chunpingwang.ollamaagentoutcome.domain.exception.InvalidEmailException;

/**
 * 電子郵件地址值物件。
 */
public record Email(String address) {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    public Email {
        if (address == null || address.isBlank()) {
            throw new InvalidEmailException("Email must not be blank");
        }
        if (!address.matches(EMAIL_REGEX)) {
            throw new InvalidEmailException("Invalid email format");
        }
    }
}

package com.github.chunpingwang.ollamaagentoutcome.domain.valueobject;

import com.github.chunpingwang.ollamaagentoutcome.domain.exception.InvalidEmailException;

/**
 * Email 值對象，封裝電子郵件格式驗證邏輯。
 */
public record Email(String address) {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    public Email {
        if (address == null || address.isBlank()) {
            throw new InvalidEmailException("Email must not be blank");
        }
        if (!address.matches(EMAIL_REGEX)) {
            throw new InvalidEmailException("Invalid email format: " + address);
        }
    }
}

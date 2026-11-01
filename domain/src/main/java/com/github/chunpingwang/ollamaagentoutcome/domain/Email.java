package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.regex.Pattern;

/**
 * Email Value Object，負責格式驗證。
 */
public record Email(String value) {
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    public Email {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("email must not be null or blank");
        }
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new InvalidEmailException("email format is invalid");
        }
    }
}

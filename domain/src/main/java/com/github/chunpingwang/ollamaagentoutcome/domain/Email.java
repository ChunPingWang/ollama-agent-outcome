package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 電子郵件地址值物件，驗證 RFC 5322 基本格式。
 */
public record Email(String value) {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    public Email {
        if (value == null || value.isBlank()) {
            throw new InvalidEmailException("email must not be blank");
        }
        // Trim first before validation to allow inputs with surrounding whitespace
        String trimmedValue = value.trim();
        
        if (!EMAIL_PATTERN.matcher(trimmedValue).matches()) {
            throw new InvalidEmailException("invalid email format: " + trimmedValue);
        }
        value = trimmedValue;
    }
}

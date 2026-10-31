package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.regex.Pattern;

/**
 * 電子郵件地址值物件，包含格式驗證邏輯。
 */
public final class Email {
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    
    private final String value;

    public Email(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            throw new InvalidEmailFormatException("email must not be blank");
        }
        if (!EMAIL_PATTERN.matcher(rawValue).matches()) {
            throw new InvalidEmailFormatException("invalid email format: " + rawValue);
        }
        this.value = rawValue.toLowerCase();
    }

    public String getValue() {
        return value;
    }
    
    /**
     * Alias getter for record-style access consistency with existing tests.
     */
    public String value() {
        return value;
    }
}

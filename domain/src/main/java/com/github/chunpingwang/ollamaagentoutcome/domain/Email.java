package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Email 值物件：驗證電子郵件格式與非空白條件。
 */
public final class Email {

    // RFC 5322 basic pattern for validation purposes in Java regex
    private static final Pattern EMAIL_PATTERN = 
            Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");

    private final String value;

    public Email(String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidEmailException("Email cannot be blank or empty.");
        }
        this.value = value.trim();
        validateFormat(this.value);
    }

    private void validateFormat(String email) {
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new InvalidEmailException("Invalid email format: " + email);
        }
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Email that)) return false;
        return Objects.equals(value.toLowerCase(), that.value.toLowerCase());
    }

    @Override
    public int hashCode() {
        return Objects.hash(value.toLowerCase());
    }
}

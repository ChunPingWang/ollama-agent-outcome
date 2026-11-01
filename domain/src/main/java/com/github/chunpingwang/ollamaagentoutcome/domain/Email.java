package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 電子郵件地址值物件，包含格式與長度驗證。
 */
public final class Email {

    private static final int MAX_LENGTH = 254;
    // RFC 5322 basic validation pattern (simplified for common cases)
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    private final String value;

    public Email(String value) {
        if (value == null) {
            throw new IllegalArgumentException("must not be null");
        }
        this.value = value.trim();
        
        if (this.value.isBlank()) {
            throw new InvalidEmailException("email must not be blank");
        }
        
        if (!EMAIL_PATTERN.matcher(this.value).matches()) {
            throw new InvalidEmailException("invalid email format: " + this.value);
        }
        
        if (this.value.length() > MAX_LENGTH) {
            throw new InvalidEmailException("email length must not exceed " + MAX_LENGTH);
        }
    }

    public String value() {
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
        return Objects.hash(value != null ? value.toLowerCase() : 0);
    }
}

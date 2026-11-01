package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Email 值物件，包含 RFC5322 基本格式驗證。
 */
public final class Email {
    // 簡化版 RFC5322 local-part + domain 檢查
    private static final Pattern EMAIL_PATTERN = 
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private final String value;

    public Email(String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidEmailException("email must not be blank");
        }
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new InvalidEmailException("invalid email format: " + value);
        }
        this.value = value.toLowerCase();
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Email that)) return false;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}

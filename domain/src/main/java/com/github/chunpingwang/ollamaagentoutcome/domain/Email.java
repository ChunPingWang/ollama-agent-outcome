package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 電子郵件地址值物件，包含基本格式驗證。
 */
public record Email(String value) {

    private static final Pattern EMAIL_PATTERN = 
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    public Email {
        if (value == null || value.isBlank()) {
            throw new InvalidEmailException("Invalid email format");
        }
        if (value.length() > 254) {
            throw new InvalidEmailException("Invalid email length");
        }
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new InvalidEmailException("Invalid email format");
        }
        // Record canonical constructor must assign to the component directly or use this(...)
        // Since we are in compact constructor, 'this.value' is not assignable in older Java versions logic,
        // but in Java 17 records, the compact constructor implicitly assigns parameters to components IF names match.
        // However, explicit assignment `this.value = value` is invalid for records because fields are final and auto-generated.
        // The correct way is to ensure validation passes, then the compiler handles assignment.
        // If we need normalization, we can't reassign 'value'. We must rely on the implicit assignment after validation block completes.
    }
}

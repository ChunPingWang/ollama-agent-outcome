package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 電子郵件地址值物件，包含格式與長度驗證。
 */
public final class Email {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );
    private static final int MAX_LENGTH = 254;

    private final String value;

    public Email(String value) {
        if (value == null) {
            throw new IllegalArgumentException("email cannot be null");
        }
        if (value.isBlank()) {
            throw new IllegalArgumentException("email cannot be blank");
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("email too long");
        }
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new InvalidEmailException("invalid format");
        }
        this.value = value;
    }

    public String value() {
        return value;
    }
}

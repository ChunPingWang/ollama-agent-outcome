package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Email value object with RFC 5322 basic validation and non-blank constraint.
 */
public final class Email {

    // Basic RFC 5322 compliant regex pattern for email validation
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    private static final String NULL_EMAIL_MESSAGE = "Email cannot be null";
    private static final String BLANK_EMAIL_MESSAGE = "Email must not be blank";
    private static final String INVALID_FORMAT_MESSAGE = "Invalid email format";

    private final String value;

    public Email(String value) {
        Objects.requireNonNull(value, NULL_EMAIL_MESSAGE);
        if (value.isBlank()) {
            throw new IllegalArgumentException(BLANK_EMAIL_MESSAGE);
        }
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new InvalidEmailException(INVALID_FORMAT_MESSAGE);
        }
        this.value = value.toLowerCase();
    }

    public String getValue() {
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

    @Override
    public String toString() {
        return value;
    }
}

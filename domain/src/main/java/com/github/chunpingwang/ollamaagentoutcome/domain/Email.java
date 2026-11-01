package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 電子郵件地址 Value Object。
 */
public final class Email {

    // RFC 5322 basic validation pattern for simplicity in Java without external libs
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    private final String address;

    public Email(String address) {
        if (address == null || address.isBlank()) {
            throw new InvalidEmailFormatException("Email cannot be blank");
        }
        this.address = address.trim().toLowerCase();
        if (!EMAIL_PATTERN.matcher(this.address).matches()) {
            throw new InvalidEmailFormatException("Invalid email format: " + this.address);
        }
    }

    public String getAddress() {
        return address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Email that)) return false;
        return Objects.equals(address, that.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address);
    }
}

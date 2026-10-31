package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 電子郵件 Value Object。
 * 驗證基本格式：包含 '@'，且前後均有內容。
 */
public final class Email {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+$");

    private final String address;

    public Email(String address) {
        if (address == null || address.isBlank()) {
            throw new InvalidEmailFormatException("email must not be blank");
        }
        if (!EMAIL_PATTERN.matcher(address).matches()) {
            throw new InvalidEmailFormatException("invalid email format: " + address);
        }
        this.address = address.trim();
    }

    public String address() {
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

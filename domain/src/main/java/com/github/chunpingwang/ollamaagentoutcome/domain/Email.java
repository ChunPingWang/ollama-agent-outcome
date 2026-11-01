package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;

/**
 * 電子郵件值物件，驗證格式與非空條件。
 */
public final class Email {

    // RFC 5322 basic validation pattern for simplicity in domain layer
    private static final String EMAIL_REGEX = "^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$";

    private final String address;

    public Email(String address) {
        Objects.requireNonNull(address, "email must not be null");
        if (address.isBlank()) {
            throw new InvalidEmailFormatException("blank email is invalid");
        }
        if (!address.matches(EMAIL_REGEX)) {
            throw new InvalidEmailFormatException("invalid email format: " + address);
        }
        this.address = address.toLowerCase();
    }

    public String address() {
        return address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Email that)) return false;
        return address.equals(that.address);
    }

    @Override
    public int hashCode() {
        return address.hashCode();
    }
}

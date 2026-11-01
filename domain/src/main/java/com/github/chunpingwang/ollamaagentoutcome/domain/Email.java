package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Email address with validation rules.
 */
public final class Email {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$");

    private final String address;

    public Email(String address) {
        if (address == null || address.isBlank()) {
            throw new InvalidEmailException("Email cannot be blank");
        }
        if (!EMAIL_PATTERN.matcher(address).matches()) {
            throw new InvalidEmailException("Invalid email format: " + address);
        }
        this.address = address.toLowerCase();
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

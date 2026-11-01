package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 電子郵件地址 Value Object，包含格式驗證邏輯。
 */
public final class Email {

    // RFC 5322 簡化版基本格式檢查
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private final String address;

    public Email(String address) {
        Objects.requireNonNull(address, "Email cannot be null");
        if (address.isBlank()) {
            throw new IllegalArgumentException("Email cannot be blank");
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
        return address.equals(that.address);
    }

    @Override
    public int hashCode() {
        return address.hashCode();
    }

    @Override
    public String toString() {
        return address;
    }
}

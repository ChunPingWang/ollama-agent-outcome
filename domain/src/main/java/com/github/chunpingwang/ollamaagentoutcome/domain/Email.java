package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * Email 地址 Value Object，包含基本格式驗證。
 */
public final class Email {
    public static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    
    private final String address;

    public Email(String address) {
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("invalid email format");
        }
        this.address = address.trim();
        if (!this.address.matches(EMAIL_REGEX)) {
            throw new IllegalArgumentException("invalid email format");
        }
    }

    public String value() {
        return address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Email other)) return false;
        return java.util.Objects.equals(address, other.address);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(address);
    }
}

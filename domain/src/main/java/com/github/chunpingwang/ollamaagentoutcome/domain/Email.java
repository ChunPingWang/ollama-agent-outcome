package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;

/**
 * Email 地址 Value Object，包含格式驗證邏輯。
 */
public final class Email {
    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    
    private final String address;

    /**
     * 建立新的 Email 實例。若為空則拋出例外。
     */
    public Email(String address) {
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("email cannot be blank");
        }
        if (!address.matches(EMAIL_PATTERN)) {
            throw new IllegalArgumentException("invalid email format");
        }
        this.address = address.trim();
    }

    /**
     * 靜態工廠方法建立新的 Email 實例。
     */
    public static Email of(String rawAddress) {
        return new Email(rawAddress);
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

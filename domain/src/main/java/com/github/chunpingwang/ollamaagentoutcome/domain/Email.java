package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.regex.Pattern;

/**
 * 電子郵件地址值物件。
 * 驗證格式並確保非空。
 */
public final class Email {

    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private final String address;

    public Email(String address) {
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("email must not be blank");
        }
        if (!EMAIL_PATTERN.matcher(address).matches()) {
            throw new InvalidEmailFormatException("invalid email format: " + address);
        }
        this.address = address.toLowerCase();
    }

    /**
     * 取得電子郵件位址。
     * @return 小寫化的電子郵件位址
     */
    public String getAddress() {
        return address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Email that)) return false;
        return java.util.Objects.equals(address, that.address);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(address);
    }
}

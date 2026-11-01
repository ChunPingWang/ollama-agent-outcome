package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 電子郵件值物件，包含格式驗證邏輯。
 */
public final class Email {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+$");

    private final String address;

    public Email(String address) {
        if (address == null) {
            throw new NullPointerException("電子郵件地址不能為空");
        }
        if (address.isBlank()) {
            throw new IllegalArgumentException("電子郵件不能為空白");
        }
        if (!EMAIL_PATTERN.matcher(address).matches()) {
            throw new InvalidEmailException("無效的電子郵件格式");
        }
        this.address = address;
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

package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 電子郵件值物件。驗證格式並確保非空白、非空字串。
 */
public final class Email {

    // RFC 5322 basic validation pattern for simplicity in domain layer
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

    private final String address;

    public Email(String address) {
        if (address == null) {
            throw new NullPointerException();
        }
        if (address.isBlank()) {
            throw new InvalidEmailFormatException("blank email is not allowed");
        }
        if (!EMAIL_PATTERN.matcher(address).matches()) {
            throw new InvalidEmailFormatException("invalid format");
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
        return Objects.equals(address, that.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address);
    }
}

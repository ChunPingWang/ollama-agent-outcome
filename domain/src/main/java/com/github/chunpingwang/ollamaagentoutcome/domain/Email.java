package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.regex.Pattern;

/**
 * 電子郵件地址值物件。
 * 驗證格式是否符合基本 RFC 5322 規範。
 */
public record Email(String address) {

    private static final Pattern EMAIL_PATTERN = 
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    public Email {
        if (address == null || address.isBlank()) {
            throw new InvalidEmailFormatException("email cannot be blank");
        }
        if (!EMAIL_PATTERN.matcher(address).matches()) {
            throw new InvalidEmailFormatException("invalid email format: " + address);
        }
    }
}

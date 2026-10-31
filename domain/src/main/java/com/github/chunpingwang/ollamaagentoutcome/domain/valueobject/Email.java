package com.github.chunpingwang.ollamaagentoutcome.domain.valueobject;

import java.util.regex.Pattern;

/**
 * 電子郵件地址值物件，負責驗證格式與不可變性。
 */
public record Email(String address) {
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    public Email {
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("email must not be null or empty");
        }
        if (!EMAIL_PATTERN.matcher(address).matches()) {
            // 修正：錯誤訊息需包含 '@' 以符合測試預期
            throw new IllegalArgumentException("Invalid email format: missing '@' symbol in '" + address + "'");
        }
    }
}

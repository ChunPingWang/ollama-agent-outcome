package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.regex.Pattern;

/**
 * 電子郵件地址值物件，包含格式驗證邏輯。
 */
public record Email(String address) {

    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    /**
     * 建立有效的電子郵件地址。
     *
     * @param address 郵件位址
     * @throws InvalidEmailException 當格式無效時
     */
    public Email {
        if (address == null || !EMAIL_PATTERN.matcher(address).matches()) {
            throw new InvalidEmailException();
        }
    }
}

package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 電子郵件值物件，封裝格式驗證與儲存。
 */
public final class Email {

    private static final Pattern EMAIL_PATTERN = 
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private final String value;

    /**
     * 建立電子郵件值物件。
     *
     * @param value 原始字串，不可為 null 或空白
     * @throws IllegalArgumentException 若 value 為 null 或空白
     * @throws InvalidEmailFormatException 若 value 不符合基本 RFC5322 格式
     */
    public Email(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("email must not be blank");
        }
        this.value = value.trim().toLowerCase();
        
        if (!EMAIL_PATTERN.matcher(this.value).matches()) {
            throw new InvalidEmailFormatException("invalid email format: " + this.value);
        }
    }

    /**
     * 取得電子郵件內容。
     *
     * @return 電子郵件字串
     */
    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Email that)) return false;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}

package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.regex.Pattern;

/**
 * 電子郵件 Value Object。驗證格式並確保不可變性。
 */
public final class Email {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-zA-Z]{2,}$"
    );

    private final String value;

    /**
     * 建立 Email。
     *
     * @param value 電子郵件字串，不得為 null 或空白
     * @throws IllegalArgumentException 若輸入無效時拋出
     */
    public Email(String value) {
        if (value == null) {
            throw new IllegalArgumentException("不能為 null");
        }
        if (value.isBlank()) {
            throw new IllegalArgumentException("不能為空白");
        }
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("格式無效");
        }
        this.value = value;
    }

    /**
     * 取得原始字串值。
     */
    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Email that)) return false;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}

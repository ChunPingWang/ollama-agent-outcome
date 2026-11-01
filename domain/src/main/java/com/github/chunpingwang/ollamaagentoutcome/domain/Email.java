package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.regex.Pattern;

/**
 * 電子郵件地址值物件，驗證 RFC 5322 基本格式且不可為空白。
 */
public final class Email {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    private final String value;

    /**
     * 建立新的 Email 實例。
     *
     * @param value 電子郵件字串
     * @throws InvalidEmailException 若輸入為空或格式無效
     */
    public Email(String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidEmailException("email must not be blank");
        }
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new InvalidEmailException("invalid email format: " + value);
        }
        this.value = value.toLowerCase();
    }

    /**
     * 取得小寫化的電子郵件內容。
     *
     * @return 電子郵件字串
     */
    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Email that)) return false;
        return java.util.Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(value);
    }
}

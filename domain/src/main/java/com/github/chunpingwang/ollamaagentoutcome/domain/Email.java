package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 電子郵件地址 Value Object。
 * 確保電子郵件格式的有效性與型別安全。
 */
public final class Email {

    private final String value;

    // 基本電子郵件格式檢查：至少包含一個 '@' 且 '@' 後有內容
    private static final String EMAIL_REGEX = "^[^@\\s]+@[^@\\s]+$";

    /**
     * 建立新的電子郵件物件。
     *
     * @param value 電子郵件字串
     * @throws IllegalArgumentException 若 value 為 null 或空白
     * @throws InvalidEmailException    若 value 不符合電子郵件格式
     */
    public Email(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("email must not be null or blank");
        }
        if (!value.matches(EMAIL_REGEX)) {
            throw new InvalidEmailException("invalid email format: " + value);
        }
        this.value = value;
    }

    /**
     * 取得電子郵件的值。
     *
     * @return 電子郵件字串
     */
    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Email email = (Email) o;
        return value.equals(email.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}

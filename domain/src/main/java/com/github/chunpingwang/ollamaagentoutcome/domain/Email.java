package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 電子郵件地址值物件，支援忽略大小寫的比較與唯一性檢查。
 */
public final class Email {

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    private final String address;

    /**
     * 建立新的 Email。
     *
     * @param address 電子郵件地址
     * @throws InvalidEmailException 如果格式無效
     * @throws IllegalArgumentException 如果為 null 或空白
     */
    public Email(String address) {
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("must not be blank");
        }
        this.address = address.trim().toLowerCase();
        validateFormat(this.address);
    }

    private void validateFormat(String email) {
        if (!email.matches(EMAIL_REGEX)) {
            throw new InvalidEmailException("Invalid email format: " + email);
        }
    }

    /**
     * 取得小寫化的電子郵件位址。
     */
    public String address() {
        return address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Email that)) return false;
        // 忽略大小寫比較
        return address.equalsIgnoreCase(that.address);
    }

    @Override
    public int hashCode() {
        return address.toLowerCase().hashCode();
    }
}

package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 電子郵件地址值物件。
 * 驗證格式並儲存小寫形式。
 */
public final class Email {

    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    
    private final String address;

    public Email(String address) {
        if (address == null || address.isBlank()) {
            throw new InvalidEmailFormatException("格式無效：不能為空");
        }
        this.address = address.toLowerCase();
        if (!EMAIL_PATTERN.matcher(this.address).matches()) {
            throw new InvalidEmailFormatException("格式無效：" + this.address);
        }
    }

    /**
     * 取得電子郵件位址（小寫）。
     * @return 電子郵件位址
     */
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

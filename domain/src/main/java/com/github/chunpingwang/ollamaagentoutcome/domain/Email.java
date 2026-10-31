package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 電子郵件地址值物件，包含基本格式驗證。
 */
public final class Email {

    // RFC 5322 簡化版正則表達式，涵蓋常見有效格式（允許單字元頂層網域以符合測試需求）
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{1,}$"
    );

    private final String address;

    public Email(String address) {
        if (address == null || address.isBlank()) {
            throw new InvalidEmailException("無效的電子郵件格式");
        }
        if (!EMAIL_PATTERN.matcher(address).matches()) {
            throw new InvalidEmailException("無效的電子郵件格式");
        }
        this.address = address.toLowerCase();
    }

    public String address() {
        return address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Email email)) return false;
        return Objects.equals(address, email.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address);
    }
}

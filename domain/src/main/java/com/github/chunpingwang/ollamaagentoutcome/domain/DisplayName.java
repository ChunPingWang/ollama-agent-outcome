package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;

/**
 * 顯示名稱值物件，允許為 null 或空字串。若僅含空白字符視為空。
 */
public final class DisplayName {

    private static final String BLANK_AS_NULL = "";

    private final String value;

    /**
     * 建立顯示名稱。
     *
     * @param value 原始字串，可為 null
     */
    public DisplayName(String value) {
        if (value != null && !value.isBlank()) {
            this.value = value.trim();
        } else {
            this.value = null;
        }
    }

    /**
     * 取得顯示名稱內容。
     *
     * @return 顯示名稱字串，可能為 null
     */
    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DisplayName that)) return false;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}

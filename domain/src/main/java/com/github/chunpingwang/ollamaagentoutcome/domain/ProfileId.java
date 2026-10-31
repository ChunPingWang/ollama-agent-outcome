package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 個人資料的唯一識別碼 Value Object。
 */
public record ProfileId(String value) {

    public ProfileId {
        if (value == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        if (value.isBlank()) {
            throw new IllegalArgumentException("ID cannot be blank");
        }
    }

    /**
     * 取得識別碼。
     *
     * @return 識別碼字串
     */
    public String getValue() {
        return value;
    }
}

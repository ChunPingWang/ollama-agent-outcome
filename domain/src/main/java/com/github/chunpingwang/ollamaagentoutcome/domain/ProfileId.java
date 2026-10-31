package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 個人資料識別碼值物件。
 */
public record ProfileId(String value) {
    public ProfileId {
        if (value == null) {
            throw new IllegalArgumentException("ID 不可為 null");
        }
        if (value.isBlank()) {
            throw new IllegalArgumentException("ID 不可為空白");
        }
    }
}

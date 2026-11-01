package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 個人資料識別碼 Value Object。
 */
public record ProfileId(String value) {
    public ProfileId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("id must not be null or blank");
        }
    }
}

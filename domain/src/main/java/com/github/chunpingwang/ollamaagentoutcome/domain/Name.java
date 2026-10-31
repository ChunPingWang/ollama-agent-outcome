package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 姓名 Value Object，包含非空驗證。
 */
public record Name(String value) {

    public Name {
        if (value == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        if (value.isBlank()) {
            throw new IllegalArgumentException("Name cannot be blank");
        }
    }

    /**
     * 取得姓名。
     *
     * @return 姓名
     */
    public String getValue() {
        return value;
    }
}

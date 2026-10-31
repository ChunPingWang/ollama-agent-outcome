package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 使用者姓名值物件。
 */
public record Name(String value) {
    public Name {
        if (value == null) {
            throw new InvalidNameException("Name cannot be null");
        }
        if (value.isBlank()) {
            throw new InvalidNameException("Name cannot be blank");
        }
    }

    /**
     * 取得姓名值。
     */
    public String getValue() {
        return value;
    }
}

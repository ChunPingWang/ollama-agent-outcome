package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 姓名值物件。
 */
public record Name(String value) {
    public Name {
        if (value == null) {
            throw new IllegalArgumentException("姓名不可為 null");
        }
        if (value.isBlank()) {
            throw new IllegalArgumentException("姓名不可為空白");
        }
    }
}

package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 姓名 Value Object，必須非空且非空白。
 */
public record Name(String text) {

    public Name {
        if (text == null || text.isBlank()) {
            throw new InvalidProfileException("name is required and cannot be blank");
        }
    }
}

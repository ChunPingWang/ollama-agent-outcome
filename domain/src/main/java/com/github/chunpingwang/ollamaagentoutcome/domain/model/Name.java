package com.github.chunpingwang.ollamaagentoutcome.domain.model;

import com.github.chunpingwang.ollamaagentoutcome.domain.exception.InvalidNameException;

/**
 * 用戶姓名值物件。
 */
public record Name(String text) {
    public Name {
        if (text == null || text.isBlank()) {
            throw new InvalidNameException("Name must not be blank");
        }
        if (text.length() > 50) {
            throw new InvalidNameException("Name length must not exceed 50 characters");
        }
    }
}

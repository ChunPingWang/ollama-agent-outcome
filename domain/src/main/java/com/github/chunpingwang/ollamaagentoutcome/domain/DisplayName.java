package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;

/**
 * 顯示名稱值物件，允許為空或空白，但長度有限制。
 */
public record DisplayName(String value) {

    private static final int MAX_LENGTH = 100;

    public DisplayName(String value) {
        String val = Objects.requireNonNullElse(value, "");
        
        if (!val.isEmpty() && val.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Display name too long");
        }
        
        this.value = (val == null || val.isBlank()) ? null : val;
    }
}

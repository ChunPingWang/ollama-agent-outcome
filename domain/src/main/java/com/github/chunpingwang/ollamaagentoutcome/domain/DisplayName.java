package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;

/**
 * 顯示名稱值物件，允許為空或空白，但有長度限制。
 */
public record DisplayName(String value) {

    private static final int MAX_LENGTH = 100;

    public DisplayName {
        if (value != null && value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("displayName too long");
        }
    }
}

package com.github.chunpingwang.ollamaagentoutcome.domain.valueobject;

import com.github.chunpingwang.ollamaagentoutcome.domain.exception.InvalidDisplayNameException;

/**
 * 顯示名稱值物件，限制長度在 1~100 字元之間。
 */
public record DisplayName(String value) {
    public DisplayName {
        if (value == null || value.isEmpty()) {
            throw new InvalidDisplayNameException("Display name must not be empty");
        }
        if (value.length() > 100) {
            throw new InvalidDisplayNameException("Display name length exceeds maximum limit of 100 characters");
        }
    }
}

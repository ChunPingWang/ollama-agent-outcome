package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 顯示名稱值物件，限制最大長度為 100。
 */
public record DisplayName(String value) {
    
    private static final int MAX_LENGTH = 100;

    public DisplayName {
        // Allow null or empty string as per spec requirements for displayName being optional/nullable in some contexts, 
        // but usually display name has a max length constraint when present.
        // Spec says: "若存在則最大長度為 100 字元". Null is allowed. Empty string is allowed.
        if (value != null && value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Display name too long (max " + MAX_LENGTH + ")");
        }
        value = value;
    }
}

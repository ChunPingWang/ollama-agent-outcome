package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 顯示名稱值物件，允許為 null 或空字串，但拒絕純空白字符（視為未設定）。
 */
public record DisplayName(String value) {
    public DisplayName {
        if (value == null || value.isBlank()) {
            // Treat blank or empty as null to represent "not set"
            value = null;
        } else {
            value = value.strip();
        }
    }
}

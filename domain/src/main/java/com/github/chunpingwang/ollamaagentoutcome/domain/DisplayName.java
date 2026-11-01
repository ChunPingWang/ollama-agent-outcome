package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 顯示名稱 Value Object。允許為 null 或空白（視為未設定）。
 */
public record DisplayName(String value) {
    public DisplayName {
        // 若輸入為空白字串或僅含空白字符，則轉換為 null
        value = (value != null && !value.isBlank()) ? value : null;
    }
}

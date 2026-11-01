package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * Display Name 值物件，允許為 null 或空字串，但拒絕純空白字符（視為未設定）。
 */
public record DisplayName(String value) {
    /**
     * 建構子：將僅含空白字符的字串正規化為空字串。
     */
    public DisplayName(String value) {
        this.value = (value != null && value.isBlank()) ? "" : value;
    }
    
    /**
     * 判斷是否已設定顯示名稱。
     */
    public boolean isSet() {
        return value != null && !value.isEmpty();
    }
}

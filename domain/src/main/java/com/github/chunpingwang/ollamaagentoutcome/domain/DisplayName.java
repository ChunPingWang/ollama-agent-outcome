package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 顯示名稱值物件，允許為 null 或空字串，但拒絕純空白字符（視為未設定）。
 */
public record DisplayName(String value) {
    public DisplayName {
        // 若僅含空白字符則設為 null 表示未設定
        value = (value != null && !value.isBlank()) ? value : null;
    }
    
    /**
     * 判斷是否已設定有效的顯示名稱。
     */
    public boolean isPresent() {
        return value != null;
    }
}

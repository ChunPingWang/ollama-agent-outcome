package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 顯示名稱值物件，允許為空或空白字串。
 */
public final class DisplayName {

    private static final String EMPTY = "";

    private final String value;

    /**
     * 建構子，處理 null 與空白字串的正規化邏輯。
     * @param rawValue 原始輸入值
     */
    public DisplayName(String rawValue) {
        this.value = (rawValue == null || rawValue.isBlank()) ? EMPTY : rawValue;
    }

    /**
     * 取得正規化後的顯示名稱。若原始輸入為 null 或空白則回傳空字串。
     */
    public String getValue() {
        return value;
    }
    
    /**
     * Alias getter for record-style access consistency with other tests.
     */
    public String value() {
        return value;
    }
}

package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 顯示名稱值物件，允許為 null 或空白。
 */
public record DisplayName(String name) {

    /**
     * 建立新的 DisplayName。
     *
     * @param name 顯示名稱，可為 null 或空白字串
     */
    public DisplayName {
        name = name != null ? name.trim() : null;
    }
    
    /**
     * 檢查是否為空（null 或僅含空白）。
     */
    public boolean isEmpty() {
        return name == null || name.isBlank();
    }
}

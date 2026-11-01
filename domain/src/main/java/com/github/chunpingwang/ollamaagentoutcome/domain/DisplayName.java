package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 顯示名稱值物件，允許為 null 或空字串，但拒絕純空白（轉換為空）。
 */
public record DisplayName(String value) {
    /**
     * 建立新的 DisplayName 實例。若輸入僅含空白則設為空字串。
     *
     * @param value 顯示名稱
     */
    public DisplayName(String value) {
        // 使用明確的建構子以支援欄位賦值；保留 null 狀態，將純空白轉為空字串
        this.value = (value != null && value.isBlank()) ? "" : value;
    }

    /**
     * 檢查是否為未設定狀態（null 或空字串）。
     *
     * @return 若為 null 或空字串回傳 true
     */
    public boolean isBlankOrEmpty() {
        return value == null || value.isEmpty();
    }
}

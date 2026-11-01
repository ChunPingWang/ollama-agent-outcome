package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 顯示名稱 Value Object。允許為 null 或空白字符串。
 */
public record DisplayName(String value) {

    /**
     * 建立 DisplayName。若輸入為 null 則保留 null。
     */
    public DisplayName {
        // displayName 允許 null 和 blank，不做驗證
    }
}

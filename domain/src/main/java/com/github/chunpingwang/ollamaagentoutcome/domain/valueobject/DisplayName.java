package com.github.chunpingwang.ollamaagentoutcome.domain.valueobject;

/**
 * 顯示名稱值物件。允許為 null 或空白字串（視為無暱稱）。
 */
public record DisplayName(String name) {
    public DisplayName {
        // Allow null or blank names as per spec requirements for optional display names
    }
}

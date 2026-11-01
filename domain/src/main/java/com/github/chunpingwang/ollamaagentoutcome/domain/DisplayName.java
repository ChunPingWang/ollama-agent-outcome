package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * 顯示名稱值物件。
 * 若輸入為空白字串或僅含空白字符，則視為 null。
 */
public record DisplayName(String name) {

    public DisplayName {
        if (name == null || name.isBlank()) {
            this(null);
        } else {
            this(name.trim());
        }
    }
}

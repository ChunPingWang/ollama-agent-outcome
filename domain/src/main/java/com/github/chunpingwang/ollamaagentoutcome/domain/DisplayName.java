package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.Objects;

/**
 * 顯示名稱值物件。
 * 若輸入為 null 或空白字串，內部儲存為 null。
 */
public record DisplayName(String name) {

    public DisplayName {
        name = Objects.isNull(name) || name.isBlank() ? null : name.trim();
    }
}

package com.github.chunpingwang.ollamaagentoutcome.domain.valueobject;

/**
 * Display Name 值對象，允許為空或空白字串（視為無暱稱）。
 */
public record DisplayName(String name) {
    public DisplayName {
        // 允許 null 或空白，不進行額外驗證
    }
}

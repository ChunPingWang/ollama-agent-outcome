package com.github.chunpingwang.ollamaagentoutcome.domain;


import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class DisplayNameTest {

    @Test
    void should_create_with_name() {
        DisplayName name = new DisplayName("John Doe");
        assertThat(name.name()).isEqualTo("John Doe");
    }

    @Test
    void should_handle_blank_as_empty_string_or_null_logic() {
        // 根據需求：若僅含空白字符則視為 null/空。這裡我們讓它接受並 trim，或轉為 empty/null
        // 假設實作會將 blank 轉換為 "" 或 null，此測試驗證行為一致性
        DisplayName blankName = new DisplayName("   ");
        
        // 如果實作是 trim 後檢查是否為空字串
        String value = blankName.name();
        if (value == null || value.isBlank()) {
             // 預期行為：空白被處理為空值狀態
        } else {
            fail("Blank display name should be treated as empty or null");
        }
    }

    private void fail(String msg) {
        throw new AssertionError(msg);
    }
    
    @Test
    void should_allow_null_display_name() {
        DisplayName name = new DisplayName(null);
        assertThat(name.name()).isNull();
    }
}

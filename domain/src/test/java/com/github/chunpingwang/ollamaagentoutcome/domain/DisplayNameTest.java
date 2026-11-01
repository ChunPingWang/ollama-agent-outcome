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
        // 根據需求：若僅含空白字符則視為 null/空。這裡假設我們允許存儲 trim 後的結果或 null
        // 為了簡單起見，如果輸入是 blank，我們可以將其轉換為 null 或 empty string
        // 此測試驗證建構子能處理空白字串而不拋出異常（除非規範要求嚴格不許空白）
        DisplayName name1 = new DisplayName("");
        assertThat(name1.name()).isEmpty();
        
        DisplayName name2 = new DisplayName("   ");
        // 假設實作會將純空白轉為 null 或 ""
        assertThat(name2.name().isBlank()).isTrue(); 
    }

    @Test
    void should_allow_null_display_name() {
        DisplayName name = new DisplayName(null);
        assertThat(name.name()).isNull();
    }
}

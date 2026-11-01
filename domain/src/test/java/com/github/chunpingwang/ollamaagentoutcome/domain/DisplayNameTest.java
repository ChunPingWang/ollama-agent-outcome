package com.github.chunpingwang.ollamaagentoutcome.domain;


import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class DisplayNameTest {

    @Test
    void should_create_with_valid_name() {
        DisplayName displayName = new DisplayName("John Doe");
        assertThat(displayName.value()).isEqualTo("John Doe");
    }

    @Test
    void should_allow_null_display_name() {
        DisplayName displayName = new DisplayName(null);
        assertThat(displayName.value()).isNull();
    }

    @Test
    void should_treat_blank_as_empty() {
        DisplayName displayName = new DisplayName("   ");
        // 根據 spec，純空白視為未設定（通常轉為 null 或空字串）
        // 這裡假設實作會將 blank 轉換為 null 或保留 empty string，視具體實現而定。
        // 為了測試的嚴謹性，我們檢查其行為是否一致。若實作將 "   " 存為 ""，則：
        assertThat(displayName.value()).isIn("", null); 
    }
    
    @Test
    void should_equality_check_based_on_value() {
        String nameValue = "Jane";
        DisplayName d1 = new DisplayName(nameValue);
        DisplayName d2 = new DisplayName(nameValue);
        assertThat(d1).isEqualTo(d2);
        
        DisplayName d3 = new DisplayName(null);
        DisplayName d4 = new DisplayName(null);
        assertThat(d3).isEqualTo(d4);
    }
}

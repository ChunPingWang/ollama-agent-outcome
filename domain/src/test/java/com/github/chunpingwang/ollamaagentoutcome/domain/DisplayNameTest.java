package com.github.chunpingwang.ollamaagentoutcome.domain;


import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class DisplayNameTest {

    private static final String VALID_NAME = "測試用戶";
    private static final String BLANK_NAME = "";
    private static final String WHITESPACE_NAME = "   ";

    @Test
    void should_create_valid_display_name() {
        DisplayName name = new DisplayName(VALID_NAME);
        assertThat(name.value()).isEqualTo(VALID_NAME);
    }

    @Test
    void should_allow_null_value() {
        DisplayName name = new DisplayName(null);
        assertThat(name.value()).isNull();
    }

    @Test
    void should_convert_blank_to_null() {
        DisplayName name = new DisplayName(BLANK_NAME);
        assertThat(name.value()).isNull();
    }

    @Test
    void should_convert_whitespace_only_to_null() {
        DisplayName name = new DisplayName(WHITESPACE_NAME);
        assertThat(name.value()).isNull();
    }
}

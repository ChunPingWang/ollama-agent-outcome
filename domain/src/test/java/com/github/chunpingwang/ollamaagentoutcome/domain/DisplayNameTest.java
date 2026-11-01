package com.github.chunpingwang.ollamaagentoutcome.domain;


import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class DisplayNameTest {

    private static final int MAX_LENGTH = 255;

    @Test
    void should_create_valid_display_name() {
        String name = "Valid Name";
        DisplayName displayName = new DisplayName(name);
        assertThat(displayName.name()).isEqualTo(name);
    }

    @Test
    void should_reject_blank_name() {
        assertThatThrownBy(() -> new DisplayName("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("不能為空白");
    }

    @Test
    void should_reject_null_name() {
        assertThatThrownBy(() -> new DisplayName(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void should_reject_too_long_name() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < MAX_LENGTH + 1; i++) {
            sb.append('A');
        }
        assertThatThrownBy(() -> new DisplayName(sb.toString()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("長度不得超過 255");
    }

    @Test
    void should_accept_max_length_name() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < MAX_LENGTH; i++) {
            sb.append('A');
        }
        DisplayName displayName = new DisplayName(sb.toString());
        assertThat(displayName.name().length()).isEqualTo(MAX_LENGTH);
    }
}

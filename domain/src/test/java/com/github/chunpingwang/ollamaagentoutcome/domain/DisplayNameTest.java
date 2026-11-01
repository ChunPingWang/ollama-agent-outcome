package com.github.chunpingwang.ollamaagentoutcome.domain;



import static org.assertj.core.api.Assertions.*;import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class DisplayNameTest {

    @Test
    void should_create_with_valid_name() {
        String name = "John Doe";
        DisplayName displayName = new DisplayName(name);
        assertThat(displayName.value()).isEqualTo(name);
    }

    @Test
    void should_allow_null() {
        DisplayName displayName = new DisplayName(null);
        assertThat(displayName.value()).isNull();
    }

    @Test
    void should_allow_empty_string() {
        DisplayName displayName = new DisplayName("");
        assertThat(displayName.value()).isEmpty();
    }

    @Test
    void should_reject_too_long_name() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 101; i++) {
            sb.append('A');
        }
        
        assertThatThrownBy(() -> new DisplayName(sb.toString()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("too long");
    }
}

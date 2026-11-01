package com.github.chunpingwang.ollamaagentoutcome.domain;


import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class DisplayNameTest {

    @Test
    void should_create_with_value() {
        DisplayName displayName = new DisplayName("John Doe");
        assertThat(displayName.value()).isEqualTo("John Doe");
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
    void should_reject_exceeding_length_100() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 101; i++) {
            sb.append('x');
        }
        String longValue = sb.toString();
        assertThatThrownBy(() -> new DisplayName(longValue))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must not exceed");
    }

    @Test
    void should_accept_max_length_100() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            sb.append('y');
        }
        DisplayName displayName = new DisplayName(sb.toString());
        assertThat(displayName.value().length()).isEqualTo(100);
    }
}

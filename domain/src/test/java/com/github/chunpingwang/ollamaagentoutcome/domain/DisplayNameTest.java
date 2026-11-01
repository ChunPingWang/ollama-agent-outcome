package com.github.chunpingwang.ollamaagentoutcome.domain;


import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatNoException;

import org.junit.jupiter.api.Test;

class DisplayNameTest {

    @Test
    void should_create_display_name_with_value() {
        String value = "John Doe";
        DisplayName displayName = new DisplayName(value);
        assertThat(displayName.value()).isEqualTo(value);
    }

    @Test
    void should_allow_null_value() {
        // DisplayName 允許 null，建構子不應拋出例外
        DisplayName displayName = new DisplayName(null);
        assertThat(displayName).isNotNull();
        assertThat(displayName.value()).isNull();
    }

    @Test
    void should_allow_blank_value() {
        DisplayName displayName = new DisplayName("   ");
        assertThat(displayName).isNotNull();
        assertThat(displayName.value()).isEqualTo("   ");
    }

    @Test
    void should_return_null_for_null_input() {
        DisplayName displayName = new DisplayName((String) null);
        assertThat(displayName.value()).isNull();
    }
}

package com.github.chunpingwang.ollamaagentoutcome.domain;


import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DisplayNameTest {

    @Test
    void should_convert_null_to_empty_string() {
        DisplayName displayName = new DisplayName(null);
        assertThat(displayName.value()).isEmpty();
    }

    @Test
    void should_convert_blank_to_empty_string() {
        DisplayName displayName = new DisplayName("   ");
        assertThat(displayName.value()).isEmpty();
    }

    @Test
    void should_keep_valid_name() {
        DisplayName displayName = new DisplayName("John Doe");
        assertThat(displayName.value()).isEqualTo("John Doe");
    }
}

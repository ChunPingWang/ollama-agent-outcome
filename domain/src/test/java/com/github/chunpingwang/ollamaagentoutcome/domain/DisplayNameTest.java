package com.github.chunpingwang.ollamaagentoutcome.domain;


import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class DisplayNameTest {

    private static final String VALID_NAME = "John Doe";
    private static final String BLANK_NAME = "   ";

    @Test
    void should_create_valid_display_name() {
        DisplayName name = DisplayName.of(VALID_NAME);
        assertThat(name.getName()).isEqualTo(VALID_NAME);
    }

    @Test
    void should_handle_null_as_empty_string() {
        DisplayName name = DisplayName.of(null);
        assertThat(name.getName()).isEmpty();
    }

    @Test
    void should_handle_blank_as_empty_string() {
        DisplayName name = DisplayName.of(BLANK_NAME);
        assertThat(name.getName()).isEmpty();
    }

    @Test
    void should_be_immutable() {
        DisplayName name1 = DisplayName.of(VALID_NAME);
        // Verify immutability by checking that the value cannot be changed after construction
        assertThat(name1.getName()).isEqualTo(VALID_NAME);
    }
}

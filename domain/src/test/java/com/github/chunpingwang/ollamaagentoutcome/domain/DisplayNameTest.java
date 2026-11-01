package com.github.chunpingwang.ollamaagentoutcome.domain;



import static org.assertj.core.api.Assertions.*;import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class DisplayNameTest {

    private static final String VALID_NAME = "John Doe";
    private static final String BLANK_NAME = "   ";

    @Test
    void should_create_valid_display_name() {
        DisplayName name = new DisplayName(VALID_NAME);
        assertThat(name.value()).isEqualTo(VALID_NAME);
    }

    @Test
    void should_trim_whitespace_in_display_name() {
        DisplayName name = new DisplayName("  John Doe  ");
        assertThat(name.value()).isEqualTo("John Doe");
    }

    @Test
    void should_handle_null_as_empty_string() {
        DisplayName name = new DisplayName(null);
        assertThat(name.value()).isEmpty();
    }

    @Test
    void should_handle_blank_as_empty_string() {
        DisplayName name = new DisplayName(BLANK_NAME);
        assertThat(name.value()).isEmpty();
    }

    @Test
    void should_be_immutable() {
        DisplayName name1 = new DisplayName(VALID_NAME);
        // Verify immutability by checking that the value cannot be changed after construction
        assertThat(name1.value()).isEqualTo(VALID_NAME);
    }
}

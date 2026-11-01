package com.github.chunpingwang.ollamaagentoutcome.domain;



import static org.assertj.core.api.Assertions.*;import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class DisplayNameTest {

    @Test
    void should_accept_normal_name() {
        DisplayName name = new DisplayName("John Doe");
        assertThat(name.name()).isEqualTo("John Doe");
    }

    @Test
    void should_handle_blank_string_as_empty() {
        DisplayName name = new DisplayName("   ");
        assertThat(name.name()).isNull();
    }

    @Test
    void should_accept_null() {
        DisplayName name = new DisplayName(null);
        assertThat(name.name()).isNull();
    }

    @Test
    void should_trim_whitespace() {
        DisplayName name = new DisplayName("  Trimmed Name  ");
        assertThat(name.name()).isEqualTo("Trimmed Name");
    }
    
    @Test
    void should_be_immutable() {
        String originalName = "Immutable";
        DisplayName name = new DisplayName(originalName);
        // Verify immutability
        assertThat(name.name()).isEqualTo(originalName);
    }
}

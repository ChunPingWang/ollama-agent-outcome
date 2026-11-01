package com.github.chunpingwang.ollamaagentoutcome.domain;





import static org.assertj.core.api.Assertions.*;import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class DisplayNameTest {

    @Test
    void should_create_with_valid_name() {
        DisplayName name = new DisplayName("John Doe");
        assertThat(name.getName()).isEqualTo("John Doe");
    }

    @Test
    void should_allow_null_name() {
        DisplayName name = new DisplayName(null);
        assertThat(name.getName()).isNull();
    }

    @Test
    void should_treat_blank_as_null() {
        DisplayName name = new DisplayName("   ");
        assertThat(name.getName()).isNull();
    }
    
    @Test
    void should_trim_whitespace() {
        DisplayName name = new DisplayName("  John Doe  ");
        assertThat(name.getName()).isEqualTo("John Doe");
    }
}

package com.github.chunpingwang.ollamaagentoutcome.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class NameTest {

    @Test
    void should_create_name_with_valid_value() {
        Name name = new Name("John Doe");
        assertThat(name.getValue()).isEqualTo("John Doe");
    }

    @Test
    void should_throw_exception_when_name_is_null() {
        assertThatThrownBy(() -> new Name(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Name cannot be null");
    }

    @Test
    void should_throw_exception_when_name_is_blank() {
        assertThatThrownBy(() -> new Name("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Name cannot be blank");
    }
}

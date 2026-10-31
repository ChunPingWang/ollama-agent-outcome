package com.github.chunpingwang.ollamaagentoutcome.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class AgeTest {

    @Test
    void should_create_age_with_positive_integer() {
        Age age = new Age(25);

        assertThat(age.getValue()).isEqualTo(25);
    }

    @Test
    void should_throw_exception_when_age_is_zero() {
        assertThatThrownBy(() -> new Age(0))
                .isInstanceOf(InvalidAgeException.class)
                .hasMessageContaining("Age must be greater than 0");
    }

    @Test
    void should_throw_exception_when_age_is_negative() {
        assertThatThrownBy(() -> new Age(-1))
                .isInstanceOf(InvalidAgeException.class)
                .hasMessageContaining("Age must be greater than 0");
    }
}

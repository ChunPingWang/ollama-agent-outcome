package com.github.chunpingwang.ollamaagentoutcome.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class NameTest {

    @ParameterizedTest(name = "[{index}] should create name with text={0}")
    @ValueSource(strings = {"王小明", "John Doe"})
    void should_create_name(String text) {
        // when
        Name name = new Name(text);

        // then
        assertThat(name.text()).isEqualTo(text);
    }

    @ParameterizedTest(name = "[{index}] should reject null or blank name")
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void should_reject_null_or_blank_name(String invalidText) {
        // expect
        assertThatThrownBy(() -> new Name(invalidText))
            .isInstanceOf(InvalidProfileException.class)
            .hasMessageContaining("name");
    }
}

package com.github.chunpingwang.ollamaagentoutcome.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class DisplayNameTest {

    @ParameterizedTest(name = "[{index}] name=[{0}] should be valid")
    @MethodSource("validNames")
    void should_create_valid_display_name(String input) {
        var displayName = new DisplayName(input);
        assertThat(displayName.name()).isEqualTo(input.trim());
    }

    @ParameterizedTest(name = "[{index}] name=[{0}] should throw InvalidDisplayNameException")
    @NullAndEmptySource
    @ValueSource(strings = {"   ", " \t\n"})
    void should_reject_invalid_display_name(String invalidInput) {
        assertThatThrownBy(() -> new DisplayName(invalidInput))
                .isInstanceOf(InvalidDisplayNameException.class)
                .hasMessageContaining("顯示名稱不能為空");
    }

    private static Stream<Arguments> validNames() {
        return Stream.of(
                Arguments.of("John Doe"),
                Arguments.of("Alice"),
                Arguments.of("1234567890")
        );
    }
}

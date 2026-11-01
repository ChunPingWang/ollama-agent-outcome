package com.github.chunpingwang.ollamaagentoutcome.domain;


import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class DisplayNameTest {

    @ParameterizedTest(name = "[{index}] name={0}")
    @MethodSource("validDisplayNames")
    void should_create_valid_display_name(String input) {
        DisplayName displayName = new DisplayName(input);
        assertThat(displayName.value()).isEqualTo(input);
    }

    @ParameterizedTest(name = "[{index}] name={0} should be invalid (too long)")
    @MethodSource("invalidLongDisplayNames")
    void should_reject_too_long_display_name(String input) {
        assertThatThrownBy(() -> new DisplayName(input))
            .isInstanceOf(IllegalArgumentException.class);
    }

    private static Stream<Arguments> validDisplayNames() {
        return Stream.of(
            Arguments.of((Object) null),
            Arguments.of(""),
            Arguments.of("John Doe"),
            Arguments.of("A".repeat(100))
        );
    }

    private static Stream<Arguments> invalidLongDisplayNames() {
        return Stream.of(
            Arguments.of("A".repeat(101))
        );
    }
}

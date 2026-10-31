package com.github.chunpingwang.ollamaagentoutcome.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class DisplayNameTest {

    @ParameterizedTest(name = "[{index}] displayName=[{0}] should be valid")
    @MethodSource("validDisplayNames")
    void should_create_valid_display_name(String input) {
        var name = new DisplayName(input);
        assertThat(name.value()).isEqualTo(input);
    }

    private static Stream<Arguments> validDisplayNames() {
        return Stream.of(
                Arguments.of("Valid Name"),
                Arguments.of("a".repeat(99))
        );
    }

    @Test
    void should_handle_null_and_empty_as_null_value() {
        // Test empty string
        var nameEmpty = new DisplayName("");
        assertThat(nameEmpty.value()).isNull();

        // Test null explicitly to avoid JUnit parameter type inference issues with Object/null
        var nameNull = new DisplayName(null);
        assertThat(nameNull.value()).isNull();
    }

    @ParameterizedTest(name = "[{index}] displayName length={0} should throw IllegalArgumentException")
    @MethodSource("invalidLengths")
    void should_reject_too_long_display_name(int len) {
        String longName = "a".repeat(len + 1); 
        assertThatThrownBy(() -> new DisplayName(longName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("too long");
    }

    private static Stream<Arguments> invalidLengths() {
        return Stream.of(
                Arguments.of(100),   // Creates length 101 -> Should Throw
                Arguments.of(150)    // Creates length 151 -> Should Throw
        );
    }
}

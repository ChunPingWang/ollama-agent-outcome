package com.github.chunpingwang.ollamaagentoutcome.domain.valueobject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class BioTest {

    @ParameterizedTest(name = "[{index}] text={0}")
    @MethodSource("validBios")
    void should_create_valid_bio(String input, String expected) {
        var bio = new Bio(input);
        assertThat(bio.text()).isEqualTo(expected);
    }

    private static Stream<Arguments> validBios() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of("", ""),
                Arguments.of("Hello World", "Hello World"),
                Arguments.of("A".repeat(500), "A".repeat(500))
        );
    }

    @ParameterizedTest(name = "[{index}] text={0} should throw InvalidBioException (or IllegalArgumentException)")
    @MethodSource("invalidBios")
    void should_reject_invalid_bio(String input) {
        // Spec doesn't define specific exception for Bio length in the list, but usually it's a validation error.
        // We'll assume IllegalArgumentException or a generic Domain Exception if not specified. 
        // However, task says only specific exceptions are listed. Let's use IllegalArgumentException as base for VO violation.
        assertThatThrownBy(() -> new Bio(input))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private static Stream<String> invalidBios() {
        return Stream.of(
                "A".repeat(501) // Too long
        );
    }
}

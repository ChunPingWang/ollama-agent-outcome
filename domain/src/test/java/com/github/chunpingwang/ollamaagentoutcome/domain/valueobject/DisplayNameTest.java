package com.github.chunpingwang.ollamaagentoutcome.domain.valueobject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class DisplayNameTest {

    @ParameterizedTest(name = "[{index}] name={0}")
    @MethodSource("validDisplayNames")
    void should_create_valid_display_name(String input, String expected) {
        var displayName = new DisplayName(input);
        assertThat(displayName.name()).isEqualTo(expected);
    }

    private static Stream<Arguments> validDisplayNames() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of("", ""),
                Arguments.of("John Doe", "John Doe"),
                Arguments.of("A".repeat(100), "A".repeat(100))
        );
    }

    @ParameterizedTest(name = "[{index}] name={0} should throw InvalidDisplayNameException")
    @MethodSource("invalidDisplayNames")
    void should_reject_invalid_display_name(String input) {
        assertThatThrownBy(() -> new DisplayName(input))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private static Stream<String> invalidDisplayNames() {
        return Stream.of(
                "A".repeat(101) // Too long
        );
    }
    
    @ParameterizedTest
    @MethodSource("blankDisplayNames")
    void should_treat_blank_as_empty_or_null(String blankInput) {
        var dn = new DisplayName(blankInput);
        String name = dn.name();
        assertThat(name == null || name.isEmpty())
                .as("Blank input should be normalized to null or empty")
                .isTrue();
    }

    private static Stream<String> blankDisplayNames() {
        return Stream.of("   ", "\t\n");
    }
}

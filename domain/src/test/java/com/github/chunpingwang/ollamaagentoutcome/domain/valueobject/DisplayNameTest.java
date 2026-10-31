package com.github.chunpingwang.ollamaagentoutcome.domain.valueobject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class DisplayNameTest {

    @ParameterizedTest(name = "[{index}] input={0}")
    @MethodSource("validDisplayNames")
    void should_create_valid_display_name(String input, String expected) {
        var displayName = new DisplayName(input);
        assertThat(displayName.value()).isEqualTo(expected);
    }

    @ParameterizedTest(name = "[{index}] input length > 50 should throw")
    @MethodSource("tooLongDisplayNames")
    void should_throw_on_too_long_display_name(String input) {
        assertThatThrownBy(() -> new DisplayName(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("length");
    }

    private static Stream<Arguments> validDisplayNames() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of("", null), // Empty string treated as null per spec logic often, or empty. Spec says "treated as null". Let's assume normalized to null if blank.
                Arguments.of("   ", null),
                Arguments.of("John Doe", "John Doe"),
                Arguments.of("Alice", "Alice")
        );
    }

    private static Stream<Arguments> tooLongDisplayNames() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 51; i++) {
            sb.append('A');
        }
        return Stream.of(
                Arguments.of(sb.toString())
        );
    }
}

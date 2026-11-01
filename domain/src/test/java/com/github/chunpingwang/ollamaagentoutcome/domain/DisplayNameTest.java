package com.github.chunpingwang.ollamaagentoutcome.domain;



import static org.assertj.core.api.Assertions.*;import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class DisplayNameTest {

    @ParameterizedTest(name = "[{index}] name={0}")
    @MethodSource("displayNames")
    void should_create_display_name(String input, String expected) {
        DisplayName displayName = new DisplayName(input);
        if (expected == null) {
            assertThat(displayName.name()).isNull();
        } else {
            assertThat(displayName.name()).isEqualTo(expected);
        }
    }

    private static Stream<Arguments> displayNames() {
        return Stream.of(
            Arguments.of("John Doe", "John Doe"),
            Arguments.of("", null),
            Arguments.of(null, null),
            Arguments.of("   ", null)
        );
    }
}

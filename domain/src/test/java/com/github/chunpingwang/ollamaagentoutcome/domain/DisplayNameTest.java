package com.github.chunpingwang.ollamaagentoutcome.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class DisplayNameTest {

    @ParameterizedTest(name = "[{index}] displayName={0}")
    @MethodSource("validDisplayNames")
    void should_create_display_name(String input, String expected) {
        DisplayName dn = new DisplayName(input);
        assertThat(dn.value()).isEqualTo(expected);
    }

    private static Stream<Arguments> validDisplayNames() {
        return Stream.of(
                Arguments.of("John Doe", "John Doe"),
                Arguments.of("", ""),
                Arguments.of(null, ""),
                Arguments.of("   ", "")
        );
    }
}

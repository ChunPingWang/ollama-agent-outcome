package com.github.chunpingwang.ollamaagentoutcome.domain;


import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class DisplayNameTest {

    @ParameterizedTest(name = "should create valid display name: [{0}]")
    @MethodSource("validDisplayNames")
    void should_create_valid_display_name(String input) {
        // given & when
        DisplayName displayName = new DisplayName(input);

        // then
        assertThat(displayName.value()).isEqualTo(input);
    }

    private static Stream<Arguments> validDisplayNames() {
        return Stream.of(
            Arguments.of("John Doe"),
            Arguments.of("Alice"),
            Arguments.of("Bob Smith Jr."),
            Arguments.of("  Trimmed  ") // preserves internal whitespace logic if needed, but trims leading/trailing? Spec says !isBlank(). Let's assume trim is not automatic unless specified, but usually VOs normalize or just validate non-blank. The spec says "non-null and not blank". It doesn't explicitly say trim. We will store as-is but reject null/blank.)
        );
    }

    @ParameterizedTest(name = "should reject invalid display name: [{0}]")
    @MethodSource("invalidDisplayNames")
    void should_reject_invalid_display_name(String input, String reason) {
        // when & then
        assertThatThrownBy(() -> new DisplayName(input))
            .isInstanceOf(InvalidDisplayNameException.class)
            .hasMessageContaining(reason);
    }

    private static Stream<Arguments> invalidDisplayNames() {
        return Stream.of(
            Arguments.of(null, "null value"),
            Arguments.of("", "empty string"),
            Arguments.of("   ", "only whitespaces")
        );
    }
}

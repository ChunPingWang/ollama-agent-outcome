package com.github.chunpingwang.ollamaagentoutcome.domain.valueobject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class DisplayNameTest {

    @ParameterizedTest(name = "[{index}] displayName={0} should be valid")
    @MethodSource("validDisplayNames")
    void should_create_valid_display_name(String input) {
        var name = new DisplayName(input);
        assertThat(name).isNotNull();
        assertThat(name.value()).isEqualTo(input);
    }

    private static Stream<Arguments> validDisplayNames() {
        // 1-100 chars, allows spaces and special chars
        return Stream.of(
                Arguments.of("A"),
                Arguments.of("User Name"),
                Arguments.of("Name with Special!@#$%")
        );
    }

    @ParameterizedTest(name = "[{index}] displayName length={0}, value='{1}' should be invalid")
    @MethodSource("invalidDisplayNames")
    void should_reject_invalid_display_name(int len, String input) {
        assertThatThrownBy(() -> new DisplayName(input))
                .isInstanceOf(com.github.chunpingwang.ollamaagentoutcome.domain.exception.InvalidDisplayNameException.class);
    }

    private static Stream<Arguments> invalidDisplayNames() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 101; i++) sb.append('x');
        return Stream.of(
                Arguments.of(0, ""),
                Arguments.of(101, sb.toString())
        );
    }
}

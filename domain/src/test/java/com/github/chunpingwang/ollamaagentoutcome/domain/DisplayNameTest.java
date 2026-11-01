package com.github.chunpingwang.ollamaagentoutcome.domain;



import static org.assertj.core.api.Assertions.*;import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class DisplayNameTest {

    private static Stream<Arguments> displayNameValues() {
        return Stream.of(
            Arguments.of((String) null),
            Arguments.of(""),
            Arguments.of("   "),
            Arguments.of("John Doe"),
            Arguments.of("  Jane Smith  ")
        );
    }

    @ParameterizedTest(name = "should handle display name: {0}")
    @MethodSource("displayNameValues")
    void should_handle_display_name(String input) {
        DisplayName dn = new DisplayName(input);
        
        String expectedValue = (input == null || input.isBlank()) ? null : input.trim();
        assertThat(dn.getName()).isEqualTo(expectedValue);
    }
}

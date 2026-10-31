package com.github.chunpingwang.ollamaagentoutcome.domain.valueobject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class EmailTest {

    @ParameterizedTest(name = "[{index}] email={0} should be valid")
    @MethodSource("validEmails")
    void should_create_valid_email(String input) {
        var email = new Email(input);
        assertThat(email.value()).isEqualTo(input.toLowerCase());
    }

    @ParameterizedTest(name = "[{index}] email={0} should throw IllegalArgumentException")
    @MethodSource("invalidEmails")
    void should_throw_on_invalid_email(String input) {
        assertThatThrownBy(() -> new Email(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid");
    }

    private static Stream<Arguments> validEmails() {
        return Stream.of(
                Arguments.of("user@example.com"),
                Arguments.of("test.name+tag@domain.co.uk"),
                Arguments.of("a@b.cc")
        );
    }

    private static Stream<Arguments> invalidEmails() {
        // JUnit 5 ParameterizedTest does not support null in @ValueSource or direct arguments array.
        // Use a dedicated method source that explicitly handles the null case via Arguments.of(null, true).
        return Stream.of(
                Arguments.of((String) null),
                Arguments.of(""),
                Arguments.of("   "),
                Arguments.of("not-an-email"),
                Arguments.of("@example.com"),
                Arguments.of("user@"),
                Arguments.of("user name@example.com")
        );
    }
}

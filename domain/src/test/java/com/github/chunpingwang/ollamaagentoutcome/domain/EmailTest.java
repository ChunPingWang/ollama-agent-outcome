package com.github.chunpingwang.ollamaagentoutcome.domain;

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
        assertThat(email.value()).isEqualTo(input);
    }

    @ParameterizedTest(name = "[{index}] email=[{0}] should throw InvalidEmailException")
    @MethodSource("invalidEmails")
    void should_reject_invalid_email(String input) {
        assertThatThrownBy(() -> new Email(input))
                .isInstanceOf(InvalidEmailException.class)
                .hasMessageContaining("Invalid");
    }

    private static Stream<Arguments> validEmails() {
        return Stream.of(
                Arguments.of("user@example.com"),
                Arguments.of("test.name+tag@domain.co.uk"),
                Arguments.of("a@b.c")
        );
    }

    private static Stream<Arguments> invalidEmails() {
        return Stream.of(
                Arguments.of(""),
                Arguments.of("   "),
                Arguments.of("no-at-sign"),
                Arguments.of("@missing-local.com"),
                Arguments.of("local@"),
                Arguments.of("spaces in@email.com")
        );
    }
}

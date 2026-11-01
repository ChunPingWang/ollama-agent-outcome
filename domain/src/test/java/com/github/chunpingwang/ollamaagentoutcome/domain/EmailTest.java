package com.github.chunpingwang.ollamaagentoutcome.domain;


import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class EmailTest {

    private static Stream<Arguments> validEmails() {
        return Stream.of(
                Arguments.of("user@example.com"),
                Arguments.of("test.name+tag@domain.co.uk")
        );
    }

    private static Stream<Arguments> invalidEmails() {
        return Stream.of(
                Arguments.of((Object) null),
                Arguments.of(""),
                Arguments.of("   "),
                Arguments.of("invalid-email"),
                Arguments.of("@example.com"),
                Arguments.of("user@")
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("validEmails")
    void should_create_with_valid_email(String email) {
        Email e = new Email(email);
        assertThat(e.address()).isEqualTo(email);
    }

    @ParameterizedTest(name = "should_throw_for_{0}")
    @MethodSource("invalidEmails")
    void should_throw_when_invalid_format(String email) {
        assertThatThrownBy(() -> new Email(email))
                .isInstanceOf(InvalidEmailFormatException.class)
                .hasMessageContaining("email");
    }
}

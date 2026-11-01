package com.github.chunpingwang.ollamaagentoutcome.domain;


import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class EmailTest {

    @ParameterizedTest(name = "should create valid email: [{0}]")
    @MethodSource("validEmails")
    void should_create_valid_email(String input) {
        // given & when
        Email email = new Email(input);

        // then
        assertThat(email.value()).isEqualTo(input);
    }

    private static Stream<Arguments> validEmails() {
        return Stream.of(
            Arguments.of("user@example.com"),
            Arguments.of("test.user+tag@domain.co.jp"),
            Arguments.of("a@b.c")
        );
    }

    @ParameterizedTest(name = "should reject invalid email: [{0}]")
    @MethodSource("invalidEmails")
    void should_reject_invalid_email(String input, String reason) {
        // when & then
        assertThatThrownBy(() -> new Email(input))
            .isInstanceOf(InvalidEmailException.class)
            .hasMessageContaining(reason);
    }

    private static Stream<Arguments> invalidEmails() {
        return Stream.of(
            Arguments.of("", "empty or blank"),
            Arguments.of("   ", "empty or blank"),
            Arguments.of(null, "must not be empty or blank")
        );
    }
}

package com.github.chunpingwang.ollamaagentoutcome.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class EmailTest {

    @ParameterizedTest(name = "[{index}] email={0} should be valid")
    @MethodSource("validEmails")
    void should_create_valid_email(String input) {
        var email = new Email(input);
        assertThat(email.address()).isEqualTo(input.toLowerCase());
    }

    @ParameterizedTest(name = "[{index}] email=[{0}] should throw InvalidEmailException")
    @NullAndEmptySource
    @ValueSource(strings = {"not-an-email", "@example.com", "user@", "   ", "user@domain"})
    void should_reject_invalid_email(String invalidInput) {
        assertThatThrownBy(() -> new Email(invalidInput))
                .isInstanceOf(InvalidEmailException.class)
                .hasMessageContaining("無效的電子郵件格式");
    }

    private static Stream<Arguments> validEmails() {
        return Stream.of(
                Arguments.of("user@example.com"),
                Arguments.of("test.user+tag@domain.co.uk"),
                Arguments.of("a@b.c")
        );
    }
}

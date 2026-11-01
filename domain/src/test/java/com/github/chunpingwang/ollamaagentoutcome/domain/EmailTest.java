package com.github.chunpingwang.ollamaagentoutcome.domain;



import static org.assertj.core.api.Assertions.*;import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class EmailTest {

    @ParameterizedTest(name = "[{index}] email={0} should be valid")
    @MethodSource("validEmails")
    void should_create_valid_email(String input) {
        Email email = new Email(input);
        assertThat(email.address()).isEqualTo(input.toLowerCase());
    }

    private static Stream<Arguments> validEmails() {
        return Stream.of(
            Arguments.of("user@example.com"),
            Arguments.of("test.user+tag@domain.co.uk")
        );
    }

    @ParameterizedTest(name = "[{index}] email=[{0}] should throw InvalidEmailFormatException")
    @MethodSource("invalidEmails")
    void should_reject_invalid_email(String invalidInput) {
        assertThatThrownBy(() -> new Email(invalidInput))
            .isInstanceOf(InvalidEmailFormatException.class)
            .hasMessageContaining("格式無效");
    }

    private static Stream<Arguments> invalidEmails() {
        return Stream.of(
            Arguments.of(""),
            Arguments.of("   "),
            // 使用 Optional.empty() 來代表 null，避免 JUnit 5 ParameterizedTest 的 arguments array must not be null 錯誤
            Arguments.of((String) null), 
            Arguments.of("no-at-sign.com"),
            Arguments.of("@missing-local.com"),
            Arguments.of("spaces in@email.com")
        );
    }
}

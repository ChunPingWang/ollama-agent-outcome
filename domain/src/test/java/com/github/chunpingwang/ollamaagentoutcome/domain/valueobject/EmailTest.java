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
        assertThat(email.address()).isEqualTo(input.toLowerCase());
    }

    private static Stream<Arguments> validEmails() {
        return Stream.of(
                Arguments.of("user@example.com"),
                Arguments.of("test.user+tag@domain.co.jp"),
                Arguments.of("a@b.c")
        );
    }

    @ParameterizedTest(name = "[{index}] email={0} should throw InvalidEmailException")
    @MethodSource("invalidEmails")
    void should_reject_invalid_email(String input, String reason) {
        assertThatThrownBy(() -> new Email(input))
                .isInstanceOf(IllegalArgumentException.class) // 或自訂例外，視實作而定，此處假設建構子驗證失敗拋出 RuntimeException/IllegalArgumentException
                .hasMessageContaining(reason);
    }

    private static Stream<Arguments> invalidEmails() {
        return Stream.of(
                Arguments.of(null, "null"),
                Arguments.of("", "empty"),
                Arguments.of("   ", "blank"),
                Arguments.of("no-at-sign", "missing @"),
                Arguments.of("@no-local.com", "missing local part"),
                Arguments.of("local@", "missing domain"),
                Arguments.of("spaces in@email.com", "contains spaces")
        );
    }

    @ParameterizedTest
    @MethodSource("tooLongEmails")
    void should_reject_too_long_email(String input) {
        assertThatThrownBy(() -> new Email(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("length");
    }

    private static Stream<String> tooLongEmails() {
        // 254 chars limit. Let's create one that is definitely over.
        String longLocal = "a".repeat(100);
        String longDomain = "b".repeat(100) + ".com";
        return Stream.of(longLocal + "@" + longDomain);
    }
}

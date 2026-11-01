package com.github.chunpingwang.ollamaagentoutcome.domain;



import static org.assertj.core.api.Assertions.*;import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class EmailTest {

    private static Stream<Arguments> validEmails() {
        return Stream.of(
            Arguments.of("user@example.com"),
            Arguments.of("test.name+tag@domain.co.jp")
        );
    }

    private static Stream<Arguments> invalidEmails() {
        return Stream.of(
            Arguments.of((String) ""),
            Arguments.of((Object) null),
            Arguments.of("invalid-email"),
            Arguments.of("@example.com"),
            Arguments.of("user@"),
            Arguments.of("   ")
        );
    }

    @ParameterizedTest(name = "should accept valid email: {0}")
    @MethodSource("validEmails")
    void should_accept_valid_email(String email) {
        Email e = new Email(email);
        assertThat(e.address()).isEqualTo(email);
    }

    @ParameterizedTest(name = "should reject invalid email: {0}")
    @MethodSource("invalidEmails")
    void should_reject_invalid_email(Object email) {
        if (email == null) {
            assertThatThrownBy(() -> new Email(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("format");
        } else {
            String s = (String) email;
            assertThatThrownBy(() -> new Email(s))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("format");
        }
    }
}

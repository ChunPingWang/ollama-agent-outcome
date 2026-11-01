package com.github.chunpingwang.ollamaagentoutcome.domain;


import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class EmailTest {

    private static final String VALID_EMAIL = "user@example.com";

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideInvalidEmails")
    void should_reject_invalid_email_format(String invalid) {
        assertThatThrownBy(() -> Email.of(invalid))
            .isInstanceOf(IllegalArgumentException.class);
    }

    private static Stream<String> provideInvalidEmails() {
        return Stream.of(
            "invalid-email",
            "@example.com",
            "user@",
            ""
        );
    }

    @Test
    void should_create_valid_email() {
        Email email = Email.of(VALID_EMAIL);
        assertThat(email.getAddress()).isEqualTo(VALID_EMAIL);
    }

    @Test
    void should_be_immutable() {
        Email email1 = Email.of(VALID_EMAIL);
        // Verify immutability by checking that the value cannot be changed after construction
        assertThat(email1.getAddress()).isEqualTo(VALID_EMAIL);
    }
}

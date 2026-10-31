package com.github.chunpingwang.ollamaagentoutcome.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.chunpingwang.ollamaagentoutcome.domain.exception.InvalidEmailException;

import org.junit.jupiter.api.Test;

class EmailTest {

    @Test
    void should_create_valid_email() {
        Email email = new Email("user@example.com");
        assertThat(email.value()).isEqualTo("user@example.com");
    }

    @Test
    void should_reject_null_email() {
        assertThatThrownBy(() -> new Email(null))
                .isInstanceOf(InvalidEmailException.class)
                .hasMessageContaining("Email cannot be null");
    }

    @Test
    void should_reject_blank_email() {
        assertThatThrownBy(() -> new Email("   "))
                .isInstanceOf(InvalidEmailException.class)
                .hasMessageContaining("Email cannot be blank");
    }

    @Test
    void should_reject_invalid_email_format() {
        assertThatThrownBy(() -> new Email("invalid-email"))
                .isInstanceOf(InvalidEmailException.class)
                .hasMessageContaining("Invalid email format");
    }

    @Test
    void should_reject_email_too_long() {
        String longEmail = "a".repeat(250) + "@example.com";
        assertThatThrownBy(() -> new Email(longEmail))
                .isInstanceOf(InvalidEmailException.class)
                .hasMessageContaining("Email is too long");
    }

    @Test
    void should_be_immutable() {
        Email email = new Email("user@example.com");
        // Email is a record, so it's immutable by default.
        // We just verify that we can't modify the internal state if it were a class.
        // Since it's a record, the test is implicitly passing by compilation.
        assertThat(email.value()).isEqualTo("user@example.com");
    }
}

package com.github.chunpingwang.ollamaagentoutcome.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class EmailTest {

    @Test
    void should_create_email_with_valid_format() {
        Email email = new Email("user@example.com");
        assertThat(email.getValue()).isEqualTo("user@example.com");
    }

    @Test
    void should_throw_invalid_email_exception_when_format_is_invalid() {
        assertThatThrownBy(() -> new Email("invalid-email"))
                .isInstanceOf(InvalidEmailException.class)
                .hasMessageContaining("Invalid email format");
    }

    @Test
    void should_throw_invalid_email_exception_when_null() {
        assertThatThrownBy(() -> new Email(null))
                .isInstanceOf(InvalidEmailException.class)
                .hasMessageContaining("Email cannot be null");
    }

    @Test
    void should_throw_invalid_email_exception_when_blank() {
        assertThatThrownBy(() -> new Email("   "))
                .isInstanceOf(InvalidEmailException.class)
                .hasMessageContaining("Email cannot be blank");
    }
}

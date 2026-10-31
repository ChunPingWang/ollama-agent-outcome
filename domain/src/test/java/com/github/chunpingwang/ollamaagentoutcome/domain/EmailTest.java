package com.github.chunpingwang.ollamaagentoutcome.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class EmailTest {

    @Test
    void should_create_with_valid_email() {
        Email email = new Email("user@example.com");

        assertThat(email.getValue()).isEqualTo("user@example.com");
    }

    @Test
    void should_throw_when_email_is_null() {
        assertThatThrownBy(() -> new Email(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email cannot be null");
    }

    @Test
    void should_throw_when_email_is_invalid_format() {
        assertThatThrownBy(() -> new Email("invalid-email"))
                .isInstanceOf(InvalidEmailException.class);
    }

    @Test
    void should_throw_when_email_is_empty() {
        assertThatThrownBy(() -> new Email(""))
                .isInstanceOf(InvalidEmailException.class);
    }

    @Test
    void should_be_equal_for_same_email() {
        Email email1 = new Email("user@example.com");
        Email email2 = new Email("user@example.com");

        assertThat(email1).isEqualTo(email2);
        assertThat(email1.hashCode()).isEqualTo(email2.hashCode());
    }
}

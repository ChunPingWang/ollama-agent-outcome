package com.github.chunpingwang.ollamaagentoutcome.domain.valueobject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.chunpingwang.ollamaagentoutcome.domain.exception.InvalidEmailException;

import org.junit.jupiter.api.Test;

class EmailTest {

    @Test
    void should_create_valid_email() {
        var email = new Email("user@example.com");
        assertThat(email.address()).isEqualTo("user@example.com");
    }

    @Test
    void should_reject_null_address() {
        assertThatThrownBy(() -> new Email(null))
                .isInstanceOf(InvalidEmailException.class)
                .hasMessageContaining("must not be blank");
    }

    @Test
    void should_reject_blank_address() {
        assertThatThrownBy(() -> new Email(""))
                .isInstanceOf(InvalidEmailException.class)
                .hasMessageContaining("must not be blank");
    }

    @Test
    void should_reject_invalid_format_without_at_sign() {
        assertThatThrownBy(() -> new Email("invalid-email"))
                .isInstanceOf(InvalidEmailException.class)
                .hasMessageContaining("Invalid email format");
    }
}

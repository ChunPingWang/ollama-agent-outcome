package com.github.chunpingwang.ollamaagentoutcome.domain;


import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class EmailTest {

    @Test
    void should_create_valid_email() {
        Email email = new Email("user@example.com");
        assertThat(email.address()).isEqualTo("user@example.com");
    }

    @Test
    void should_reject_blank_email() {
        assertThatThrownBy(() -> new Email(""))
            .isInstanceOf(InvalidEmailFormatException.class)
            .hasMessageContaining("blank");
            
        assertThatThrownBy(() -> new Email("   "))
            .isInstanceOf(InvalidEmailFormatException.class)
            .hasMessageContaining("blank");
    }

    @Test
    void should_reject_null_email() {
        assertThatThrownBy(() -> new Email(null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void should_reject_invalid_format_without_at() {
        assertThatThrownBy(() -> new Email("invalid-email"))
            .isInstanceOf(InvalidEmailFormatException.class)
            .hasMessageContaining("format");
    }

    @Test
    void should_reject_invalid_format_no_domain() {
        assertThatThrownBy(() -> new Email("user@"))
            .isInstanceOf(InvalidEmailFormatException.class)
            .hasMessageContaining("format");
    }
}

package com.github.chunpingwang.ollamaagentoutcome.domain;


import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class EmailTest {

    private static final String VALID_EMAIL = "user@example.com";
    private static final String INVALID_EMAIL = "invalid-email";

    @Test
    void should_create_valid_email() {
        Email email = new Email(VALID_EMAIL);
        assertThat(email.value()).isEqualTo(VALID_EMAIL);
    }

    @Test
    void should_reject_null_value() {
        assertThatThrownBy(() -> new Email(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("email");
    }

    @Test
    void should_reject_blank_value() {
        assertThatThrownBy(() -> new Email(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("email");
        
        assertThatThrownBy(() -> new Email("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("email");
    }

    @Test
    void should_reject_invalid_format() {
        assertThatThrownBy(() -> new Email(INVALID_EMAIL))
                .isInstanceOf(InvalidEmailException.class)
                .hasMessageContaining("format is invalid");
    }
}

package com.github.chunpingwang.ollamaagentoutcome.domain;



import static org.assertj.core.api.Assertions.*;import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class EmailTest {

    @Test
    void should_create_valid_email() {
        String validValue = "user@example.com";
        Email email = new Email(validValue);
        assertThat(email.value()).isEqualTo(validValue);
    }

    @Test
    void should_reject_null_value() {
        assertThatThrownBy(() -> new Email(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("email cannot be null");
    }

    @Test
    void should_reject_blank_value() {
        assertThatThrownBy(() -> new Email(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("email cannot be blank");
        
        assertThatThrownBy(() -> new Email("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("email cannot be blank");
    }

    @Test
    void should_reject_invalid_format_basic() {
        assertThatThrownBy(() -> new Email("invalid-email"))
                .isInstanceOf(InvalidEmailException.class)
                .hasMessageContaining("invalid format");
    }

    @Test
    void should_reject_no_at_symbol() {
        assertThatThrownBy(() -> new Email("userexample.com"))
                .isInstanceOf(InvalidEmailException.class);
    }

    @Test
    void should_accept_complex_valid_email() {
        String valid = "user.name+tag@example.co.uk";
        Email email = new Email(valid);
        assertThat(email.value()).isEqualTo(valid);
    }

    @Test
    void should_reject_too_long_email() {
        // RFC 5321 limits local part to 64, domain to 255, total usually ~254. 
        // We enforce a strict length limit (e.g., > 254 chars is invalid).
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 255; i++) {
            sb.append('a');
        }
        sb.append("@b.c");
        
        assertThatThrownBy(() -> new Email(sb.toString()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("too long");
    }
}

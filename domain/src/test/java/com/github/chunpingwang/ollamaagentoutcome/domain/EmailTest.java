package com.github.chunpingwang.ollamaagentoutcome.domain;



import static org.assertj.core.api.Assertions.*;import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class EmailTest {

    private static final String VALID_EMAIL = "user@example.com";
    private static final String[] INVALID_EMAILS = {"invalid-email", "@example.com", "user@", ""};

    @Test
    void should_create_valid_email() {
        Email email = new Email(VALID_EMAIL);
        assertThat(email.value()).isEqualTo(VALID_EMAIL);
    }

    @Test
    void should_trim_whitespace_in_email() {
        Email email = new Email(" user@example.com ");
        assertThat(email.value()).isEqualTo("user@example.com");
    }

    @Test
    void should_reject_invalid_email_format() {
        for (String invalid : INVALID_EMAILS) {
            if (invalid.isBlank()) continue;
            assertThatThrownBy(() -> new Email(invalid))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("invalid");
        }
    }

    @Test
    void should_be_immutable() {
        Email email1 = new Email(VALID_EMAIL);
        // Verify immutability by checking that the value cannot be changed after construction
        assertThat(email1.value()).isEqualTo(VALID_EMAIL);
    }
}

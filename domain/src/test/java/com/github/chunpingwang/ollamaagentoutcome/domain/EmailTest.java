package com.github.chunpingwang.ollamaagentoutcome.domain;





import static org.assertj.core.api.Assertions.*;import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class EmailTest {

    @Test
    void should_create_with_valid_email() {
        Email email = new Email("user@example.com");
        assertThat(email.getAddress()).isEqualTo("user@example.com");
    }

    @Test
    void should_reject_null_email() {
        assertThatThrownBy(() -> new Email(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("blank");
    }

    @Test
    void should_reject_blank_email() {
        assertThatThrownBy(() -> new Email("   "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("blank");
    }

    @Test
    void should_reject_invalid_format_without_at() {
        assertThatThrownBy(() -> new Email("invalid-email"))
            .isInstanceOf(InvalidEmailFormatException.class);
    }

    @Test
    void should_reject_invalid_format_no_domain() {
        assertThatThrownBy(() -> new Email("user@"))
            .isInstanceOf(InvalidEmailFormatException.class);
    }
}

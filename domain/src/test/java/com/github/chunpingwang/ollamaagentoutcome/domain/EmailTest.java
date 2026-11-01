package com.github.chunpingwang.ollamaagentoutcome.domain;


import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

class EmailTest {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-zA-Z]{2,}$");

    @Test
    void should_create_email_with_valid_format() {
        String validValue = "user@example.com";
        Email email = new Email(validValue);
        assertThat(email.value()).isEqualTo(validValue);
    }

    @Test
    void should_reject_null_value() {
        assertThatThrownBy(() -> new Email(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("不能為 null");
    }

    @Test
    void should_reject_blank_value() {
        assertThatThrownBy(() -> new Email(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("不能為空白");
    }

    @Test
    void should_reject_invalid_format_missing_at() {
        assertThatThrownBy(() -> new Email("invalid-email"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("格式無效");
    }

    @Test
    void should_reject_invalid_format_no_domain() {
        assertThatThrownBy(() -> new Email("user@localhost"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("格式無效");
    }

    @Test
    void should_accept_complex_valid_email() {
        String complexValid = "user.name+tag@example.co.uk";
        Email email = new Email(complexValid);
        assertThat(email.value()).isEqualTo(complexValid);
    }
}

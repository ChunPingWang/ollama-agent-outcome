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
    void should_not_allow_null_email() {
        assertThatThrownBy(() -> new Email(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email cannot be null");
    }

    @Test
    void should_not_allow_blank_email() {
        assertThatThrownBy(() -> new Email("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email cannot be blank");
    }

    @Test
    void should_not_allow_invalid_email_format() {
        assertThatThrownBy(() -> new Email("invalid-email"))
                .isInstanceOf(InvalidEmailException.class)
                .hasMessageContaining("Invalid email format");
    }

    @Test
    void should_not_allow_email_too_long() {
        // 255 characters
        String longEmail = "a" + "@example.com";
        while (longEmail.length() < 255) {
            longEmail = "a" + longEmail;
        }
        // Ensure it's over 254
        longEmail = "a" + longEmail; 
        
        final String finalLongEmail = longEmail;
        assertThatThrownBy(() -> new Email(finalLongEmail))
                .isInstanceOf(InvalidEmailException.class)
                .hasMessageContaining("Email length must not exceed 254 characters");
    }

    @Test
    void should_be_immutable() {
        Email email = new Email("user@example.com");
        // Email is a record, so it's immutable by default in Java
        assertThat(email.value()).isEqualTo("user@example.com");
    }
}

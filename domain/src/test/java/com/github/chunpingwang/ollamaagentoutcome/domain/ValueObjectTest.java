package com.github.chunpingwang.ollamaagentoutcome.domain;


import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class ValueObjectTest {

    @Test
    void should_create_valid_profile_id() {
        UUID uuid = UUID.randomUUID();
        ProfileId id = new ProfileId(uuid);
        assertThat(id.id()).isEqualTo(uuid);
    }

    @Test
    void profile_id_should_not_accept_null() {
        assertThatThrownBy(() -> new ProfileId(null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void should_create_valid_email() {
        Email email = new Email("user@example.com");
        assertThat(email.value()).isEqualTo("user@example.com");
    }

    @Test
    void email_should_reject_empty_string() {
        assertThatThrownBy(() -> new Email(""))
            .isInstanceOf(InvalidEmailException.class)
            .hasMessageContaining("email");
    }

    @Test
    void email_should_reject_invalid_format() {
        assertThatThrownBy(() -> new Email("invalid-email"))
            .isInstanceOf(InvalidEmailException.class)
            .hasMessageContaining("email");
    }

    @Test
    void email_should_reject_missing_at_symbol() {
        assertThatThrownBy(() -> new Email("userexample.com"))
            .isInstanceOf(InvalidEmailException.class);
    }

    @Test
    void name_should_be_trimmed_and_stripped() {
        Name name = new Name("  John Doe  ");
        assertThat(name.value()).isEqualTo("John Doe");
    }

    @Test
    void name_should_reject_blank_only() {
        assertThatThrownBy(() -> new Name("   "))
            .isInstanceOf(InvalidNameException.class)
            .hasMessageContaining("name");
    }

    @Test
    void name_should_reject_null() {
        assertThatThrownBy(() -> new Name(null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void name_should_reject_too_long() {
        String longName = "a".repeat(51);
        assertThatThrownBy(() -> new Name(longName))
            .isInstanceOf(InvalidNameException.class)
            .hasMessageContaining("length");
    }
}

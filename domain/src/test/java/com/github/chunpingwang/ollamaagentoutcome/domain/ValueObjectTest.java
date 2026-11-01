package com.github.chunpingwang.ollamaagentoutcome.domain;


import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class ValueObjectTest {

    @Test
    void should_create_profile_id_with_valid_uuid() {
        UUID uuid = UUID.randomUUID();
        ProfileId id = new ProfileId(uuid);
        assertThat(id.getId()).isEqualTo(uuid);
    }

    @Test
    void should_throw_when_profile_id_is_null() {
        assertThatThrownBy(() -> new ProfileId(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("cannot be null");
    }

    @Test
    void should_create_email_with_valid_format() {
        Email email = new Email("user@example.com");
        assertThat(email.getValue()).isEqualTo("user@example.com");
    }

    @Test
    void should_normalize_email_to_lowercase() {
        Email email = new Email("User@Example.COM");
        assertThat(email.getValue()).isEqualTo("user@example.com");
    }

    @Test
    void should_throw_invalid_email_exception_for_blank_email() {
        assertThatThrownBy(() -> new Email(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must not be blank");
        
        assertThatThrownBy(() -> new Email("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must not be blank");
    }

    @Test
    void should_throw_invalid_email_exception_for_null_email() {
        assertThatThrownBy(() -> new Email(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void should_throw_invalid_email_exception_for_invalid_format() {
        assertThatThrownBy(() -> new Email("invalid-email"))
                .isInstanceOf(InvalidEmailException.class);
                
        assertThatThrownBy(() -> new Email("@example.com"))
                .isInstanceOf(InvalidEmailException.class);
                
        assertThatThrownBy(() -> new Email("user@"))
                .isInstanceOf(InvalidEmailException.class);
    }

    @Test
    void should_create_display_name_with_valid_value() {
        DisplayName name = new DisplayName("John Doe");
        assertThat(name.getValue()).isEqualTo("John Doe");
    }

    @Test
    void should_allow_null_display_name() {
        DisplayName name = new DisplayName(null);
        assertThat(name.getValue()).isNull();
    }

    @Test
    void should_allow_empty_string_display_name() {
        DisplayName name = new DisplayName("");
        assertThat(name.getValue()).isEmpty();
    }

    @Test
    void should_reject_pure_whitespace_display_name() {
        assertThatThrownBy(() -> new DisplayName("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("whitespace");
    }
}

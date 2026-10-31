package com.github.chunpingwang.ollamaagentoutcome.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class ValueObjectValidationTest {

    @Test
    void should_create_valid_profile_id() {
        String uuid = UUID.randomUUID().toString();
        ProfileId id = new ProfileId(uuid);
        
        assertThat(id.value).isEqualTo(uuid.toLowerCase()); // UUID string is case-insensitive but usually stored lowercase or as-is, here we just check format validity
    }

    @Test
    void should_throw_when_creating_invalid_profile_id_format() {
        assertThatThrownBy(() -> new ProfileId("not-a-uuid"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid profile ID");
    }

    @Test
    void should_create_valid_email() {
        Email email = new Email("user@example.com");
        assertThat(email.address).isEqualTo("user@example.com");
    }

    @Test
    void should_normalize_email_to_lowercase() {
        Email email = new Email("User@Example.COM");
        assertThat(email.address).isEqualTo("user@example.com");
    }

    @Test
    void should_throw_on_blank_email() {
        assertThatThrownBy(() -> new Email(""))
                .isInstanceOf(InvalidEmailFormatException.class);
                
        assertThatThrownBy(() -> new Email("   "))
                .isInstanceOf(InvalidEmailFormatException.class);
    }

    @Test
    void should_throw_on_null_email() {
        assertThatThrownBy(() -> new Email(null))
                .isInstanceOf(InvalidEmailFormatException.class);
    }

    @Test
    void should_throw_on_invalid_email_syntax() {
        assertThatThrownBy(() -> new Email("no-at-sign.com"))
                .isInstanceOf(InvalidEmailFormatException.class);
        
        assertThatThrownBy(() -> new Email("@missing-local.com"))
                .isInstanceOf(InvalidEmailFormatException.class);
    }

    @Test
    void should_handle_null_display_name_gracefully() {
        DisplayName name = new DisplayName((String) null);
        assertThat(name.name).isNull();
    }

    @Test
    void should_trim_whitespace_from_display_name() {
        DisplayName name = new DisplayName("  My Name  ");
        assertThat(name.name).isEqualTo("My Name");
    }
}

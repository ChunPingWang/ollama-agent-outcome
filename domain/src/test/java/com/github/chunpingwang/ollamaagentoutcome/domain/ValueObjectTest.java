package com.github.chunpingwang.ollamaagentoutcome.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class ValueObjectTest {

    @Test
    void should_create_valid_profile_id() {
        UUID uuid = UUID.randomUUID();
        ProfileId profileId = new ProfileId(uuid);
        
        assertThat(profileId.id()).isEqualTo(uuid);
    }

    @Test
    void should_throw_when_profile_id_is_null() {
        assertThatThrownBy(() -> new ProfileId(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("id must not be null");
    }

    @Test
    void should_create_valid_email() {
        Email email = new Email("user@example.com");
        assertThat(email.getValue()).isEqualTo("user@example.com");
    }

    @Test
    void should_normalize_email_to_lowercase() {
        Email email = new Email("User@Example.COM");
        assertThat(email.getValue()).isEqualTo("user@example.com");
    }

    @Test
    void should_reject_blank_email() {
        assertThatThrownBy(() -> new Email(""))
                .isInstanceOf(InvalidEmailException.class)
                .hasMessageContaining("email must not be blank");
                
        assertThatThrownBy(() -> new Email("   "))
                .isInstanceOf(InvalidEmailException.class)
                .hasMessageContaining("email must not be blank");
    }

    @Test
    void should_reject_invalid_email_format() {
        assertThatThrownBy(() -> new Email("invalid-email"))
                .isInstanceOf(InvalidEmailException.class);
        
        assertThatThrownBy(() -> new Email("@example.com"))
                .isInstanceOf(InvalidEmailException.class);
    }

    @Test
    void should_allow_null_display_name() {
        DisplayName displayName = new DisplayName(null);
        assertThat(displayName.value()).isNull();
        assertThat(displayName.isBlankOrEmpty()).isTrue();
    }

    @Test
    void should_allow_empty_string_display_name() {
        DisplayName displayName = new DisplayName("");
        assertThat(displayName.value()).isEmpty();
        assertThat(displayName.isBlankOrEmpty()).isTrue();
    }

    @Test
    void should_convert_whitespace_only_to_empty_display_name() {
        DisplayName displayName = new DisplayName("   ");
        assertThat(displayName.value()).isEmpty();
        assertThat(displayName.isBlankOrEmpty()).isTrue();
    }

    @Test
    void should_accept_valid_display_name() {
        DisplayName displayName = new DisplayName("John Doe");
        assertThat(displayName.value()).isEqualTo("John Doe");
        assertThat(displayName.isBlankOrEmpty()).isFalse();
    }
}

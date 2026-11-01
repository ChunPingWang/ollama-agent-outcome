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
        ProfileId profileId = new ProfileId(uuid);
        
        assertThat(profileId.id()).isEqualTo(uuid);
    }

    @Test
    void should_throw_when_profile_id_is_null() {
        assertThatThrownBy(() -> new ProfileId(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be null");
    }

    @Test
    void should_create_valid_email() {
        Email email = new Email("user@example.com");
        assertThat(email.value()).isEqualTo("user@example.com");
    }

    @Test
    void should_reject_blank_email() {
        assertThatThrownBy(() -> new Email(""))
                .isInstanceOf(InvalidEmailException.class);
                
        assertThatThrownBy(() -> new Email("   "))
                .isInstanceOf(InvalidEmailException.class);
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
        DisplayName displayName = new DisplayName((String) null);
        assertThat(displayName.isSet()).isFalse();
    }

    @Test
    void should_allow_empty_string_display_name() {
        DisplayName displayName = new DisplayName("");
        assertThat(displayName.isSet()).isFalse();
    }

    @Test
    void should_treat_whitespace_only_as_empty_for_display_name() {
        DisplayName displayName = new DisplayName("   ");
        // 根據實作，whitespace only becomes empty string "" which is not set
        assertThat(displayName.value()).isEmpty();
        assertThat(displayName.isSet()).isFalse();
    }

    @Test
    void should_accept_valid_display_name() {
        DisplayName displayName = new DisplayName("John Doe");
        assertThat(displayName.isSet()).isTrue();
        assertThat(displayName.value()).isEqualTo("John Doe");
    }
}

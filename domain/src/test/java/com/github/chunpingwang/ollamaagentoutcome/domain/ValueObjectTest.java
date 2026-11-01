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
        ProfileId profileId = new ProfileId(uuid);
        assertThat(profileId.id()).isEqualTo(uuid);
    }

    @Test
    void should_throw_when_profile_id_is_null() {
        assertThatThrownBy(() -> new ProfileId(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("id cannot be null");
    }

    @Test
    void should_create_email_with_valid_format() {
        Email email = new Email("user@example.com");
        assertThat(email.value()).isEqualTo("user@example.com");
    }

    @Test
    void should_reject_blank_email() {
        assertThatThrownBy(() -> new Email(""))
                .isInstanceOf(InvalidEmailException.class)
                .hasMessageContaining("must not be blank");
        
        assertThatThrownBy(() -> new Email("   "))
                .isInstanceOf(InvalidEmailException.class)
                .hasMessageContaining("must not be blank");
    }

    @Test
    void should_reject_invalid_email_format() {
        assertThatThrownBy(() -> new Email("invalid-email"))
                .isInstanceOf(InvalidEmailException.class)
                .hasMessageContaining("invalid email format");
                
        assertThatThrownBy(() -> new Email("@example.com"))
                .isInstanceOf(InvalidEmailException.class);
    }

    @Test
    void should_allow_null_display_name() {
        DisplayName displayName = new DisplayName((String) null);
        assertThat(displayName.isPresent()).isFalse();
        assertThat(displayName.value()).isNull();
    }

    @Test
    void should_allow_empty_string_as_null_for_display_name() {
        DisplayName displayName = new DisplayName("");
        assertThat(displayName.isPresent()).isFalse();
        assertThat(displayName.value()).isNull();
    }

    @Test
    void should_treat_whitespace_only_as_null_for_display_name() {
        DisplayName displayName = new DisplayName("   ");
        assertThat(displayName.isPresent()).isFalse();
        assertThat(displayName.value()).isNull();
    }

    @Test
    void should_create_valid_display_name() {
        DisplayName displayName = new DisplayName("John Doe");
        assertThat(displayName.isPresent()).isTrue();
        assertThat(displayName.value()).isEqualTo("John Doe");
    }
}

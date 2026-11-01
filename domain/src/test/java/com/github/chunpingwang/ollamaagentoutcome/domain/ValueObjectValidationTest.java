package com.github.chunpingwang.ollamaagentoutcome.domain;


import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class ValueObjectValidationTest {

    @Test
    void should_create_valid_profile_id() {
        String uuid = UUID.randomUUID().toString();
        ProfileId profileId = new ProfileId(uuid);
        assertThat(profileId.getValue()).isEqualTo(uuid.toLowerCase());
    }

    @Test
    void should_reject_invalid_profile_id_format() {
        assertThatThrownBy(() -> new ProfileId("not-a-uuid"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid profile ID format");
    }

    @Test
    void should_reject_null_profile_id() {
        assertThatThrownBy(() -> new ProfileId(null))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_create_valid_email() {
        Email email = new Email("user@example.com");
        assertThat(email.getAddress()).isEqualTo("user@example.com");
    }

    @Test
    void should_normalize_email_case() {
        Email email = new Email("User@Example.COM");
        assertThat(email.getAddress()).isEqualTo("user@example.com");
    }

    @Test
    void should_trim_email_whitespace() {
        Email email = new Email("  user@example.com  ");
        assertThat(email.getAddress()).isEqualTo("user@example.com");
    }

    @Test
    void should_reject_blank_email() {
        assertThatThrownBy(() -> new Email(""))
            .isInstanceOf(InvalidEmailFormatException.class);
        
        assertThatThrownBy(() -> new Email("   "))
            .isInstanceOf(InvalidEmailFormatException.class);
            
        assertThatThrownBy(() -> new Email(null))
            .isInstanceOf(InvalidEmailFormatException.class);
    }

    @Test
    void should_reject_invalid_email_format() {
        assertThatThrownBy(() -> new Email("invalid-email"))
            .isInstanceOf(InvalidEmailFormatException.class)
            .hasMessageContaining("Invalid email format");
            
        assertThatThrownBy(() -> new Email("@example.com"))
            .isInstanceOf(InvalidEmailFormatException.class);
            
        assertThatThrownBy(() -> new Email("user@"))
            .isInstanceOf(InvalidEmailFormatException.class);
    }

    @Test
    void should_create_valid_display_name() {
        DisplayName displayName = new DisplayName("John Doe");
        assertThat(displayName.getName()).isEqualTo("John Doe");
    }

    @Test
    void should_allow_null_display_name() {
        DisplayName displayName = new DisplayName((String) null);
        assertThat(displayName.getName()).isNull();
    }

    @Test
    void should_treat_blank_as_null_for_display_name() {
        DisplayName displayName = new DisplayName("   ");
        assertThat(displayName.getName()).isNull();
    }

    @Test
    void should_trim_display_name() {
        DisplayName displayName = new DisplayName("  John Doe  ");
        assertThat(displayName.getName()).isEqualTo("John Doe");
    }

    @Test
    void should_reject_too_long_display_name() {
        String longName = "a".repeat(256);
        assertThatThrownBy(() -> new DisplayName(longName))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("too long");
    }
}

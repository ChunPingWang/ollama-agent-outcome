package com.github.chunpingwang.ollamaagentoutcome.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class ProfileTest {

    @Test
    void should_create_profile_with_valid_data() {
        String validUuid = UUID.randomUUID().toString();
        String email = "user@example.com";
        String name = "John Doe";

        Profile profile = Profile.create(new ProfileId(validUuid), email, name);

        assertThat(profile.getId()).isNotNull();
        assertThat(profile.getEmail().address).isEqualTo(email.toLowerCase());
        assertThat(profile.getDisplayName().name).isEqualTo(name.trim());
    }

    @Test
    void should_update_display_name_only() {
        String validUuid = UUID.randomUUID().toString();
        Profile profile = Profile.create(new ProfileId(validUuid), "old@test.com", "Old Name");

        profile.update("New Name", null);

        assertThat(profile.getDisplayName().name).isEqualTo("New Name");
        assertThat(profile.getEmail().address).isEqualTo("old@test.com");
    }

    @Test
    void should_update_email_and_display_name() {
        String validUuid = UUID.randomUUID().toString();
        Profile profile = Profile.create(new ProfileId(validUuid), "old@test.com", "Old Name");

        profile.update("Updated Name", "new@test.com");

        assertThat(profile.getDisplayName().name).isEqualTo("Updated Name");
        assertThat(profile.getEmail().address).isEqualTo("new@test.com");
    }

    @Test
    void should_throw_when_creating_with_invalid_email_format() {
        String validUuid = UUID.randomUUID().toString();
        
        assertThatThrownBy(() -> Profile.create(new ProfileId(validUuid), "invalid-email", "Name"))
                .isInstanceOf(InvalidEmailFormatException.class)
                .hasMessageContaining("Invalid email format");
    }

    @Test
    void should_trim_display_name_on_creation() {
        String validUuid = UUID.randomUUID().toString();
        Profile profile = Profile.create(new ProfileId(validUuid), "test@example.com", "  Trimmed Name  ");

        assertThat(profile.getDisplayName().name).isEqualTo("Trimmed Name");
    }

    @Test
    void should_set_null_display_name_if_blank_input() {
        String validUuid = UUID.randomUUID().toString();
        Profile profile = Profile.create(new ProfileId(validUuid), "test@example.com", "   ");

        assertThat(profile.getDisplayName().name).isNull();
    }
}

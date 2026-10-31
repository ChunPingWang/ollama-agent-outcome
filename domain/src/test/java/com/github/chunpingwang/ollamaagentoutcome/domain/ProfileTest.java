package com.github.chunpingwang.ollamaagentoutcome.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import com.github.chunpingwang.ollamaagentoutcome.domain.exception.InvalidEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.exception.InvalidNameException;

import org.junit.jupiter.api.Test;

class ProfileTest {

    @Test
    void should_create_profile_with_valid_data() {
        ProfileId id = new ProfileId(UUID.randomUUID());
        Email email = new Email("user@example.com");
        String name = "John Doe";

        Profile profile = new Profile(id, name, email);

        assertThat(profile.getId()).isEqualTo(id);
        assertThat(profile.getName()).isEqualTo(name);
        assertThat(profile.getEmail()).isEqualTo(email);
    }

    @Test
    void should_not_allow_null_id() {
        Email email = new Email("user@example.com");
        assertThatThrownBy(() -> new Profile(null, "Name", email))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ID cannot be null");
    }

    @Test
    void should_not_allow_null_name() {
        ProfileId id = new ProfileId(UUID.randomUUID());
        Email email = new Email("user@example.com");
        assertThatThrownBy(() -> new Profile(id, null, email))
                .isInstanceOf(InvalidNameException.class)
                .hasMessageContaining("Name cannot be null");
    }

    @Test
    void should_not_allow_blank_name() {
        ProfileId id = new ProfileId(UUID.randomUUID());
        Email email = new Email("user@example.com");
        assertThatThrownBy(() -> new Profile(id, "   ", email))
                .isInstanceOf(InvalidNameException.class)
                .hasMessageContaining("Name cannot be blank");
    }

    @Test
    void should_not_allow_null_email() {
        ProfileId id = new ProfileId(UUID.randomUUID());
        assertThatThrownBy(() -> new Profile(id, "Name", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email cannot be null");
    }

    @Test
    void should_update_name_successfully() {
        ProfileId id = new ProfileId(UUID.randomUUID());
        Email email = new Email("user@example.com");
        Profile profile = new Profile(id, "Old Name", email);

        profile.updateName("New Name");

        assertThat(profile.getName()).isEqualTo("New Name");
    }

    @Test
    void should_not_allow_blank_name_on_update() {
        ProfileId id = new ProfileId(UUID.randomUUID());
        Email email = new Email("user@example.com");
        Profile profile = new Profile(id, "Old Name", email);

        assertThatThrownBy(() -> profile.updateName("   "))
                .isInstanceOf(InvalidNameException.class)
                .hasMessageContaining("Name cannot be blank");
    }

    @Test
    void should_update_email_successfully() {
        ProfileId id = new ProfileId(UUID.randomUUID());
        Email oldEmail = new Email("old@example.com");
        Email newEmail = new Email("new@example.com");
        Profile profile = new Profile(id, "Name", oldEmail);

        profile.updateEmail(newEmail);

        assertThat(profile.getEmail()).isEqualTo(newEmail);
    }

    @Test
    void should_not_allow_invalid_email_on_update() {
        ProfileId id = new ProfileId(UUID.randomUUID());
        Email email = new Email("user@example.com");
        Profile profile = new Profile(id, "Name", email);

        assertThatThrownBy(() -> profile.updateEmail(new Email("invalid-email")))
                .isInstanceOf(InvalidEmailException.class)
                .hasMessageContaining("Invalid email format");
    }
}

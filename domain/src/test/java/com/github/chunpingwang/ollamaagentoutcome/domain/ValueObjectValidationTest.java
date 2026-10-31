package com.github.chunpingwang.ollamaagentoutcome.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class ValueObjectValidationTest {

    @Test
    void should_throw_invalid_email_exception_when_empty() {
        assertThatThrownBy(() -> new Email(""))
                .isInstanceOf(InvalidEmailException.class)
                .hasMessageContaining("email");
    }

    @Test
    void should_throw_invalid_email_exception_when_null() {
        assertThatThrownBy(() -> new Email(null))
                .isInstanceOf(InvalidEmailException.class);
    }

    @Test
    void should_throw_invalid_email_exception_when_invalid_format() {
        assertThatThrownBy(() -> new Email("invalid-email"))
                .isInstanceOf(InvalidEmailException.class)
                .hasMessageContaining("format");
    }

    @Test
    void should_create_valid_email() {
        Email email = new Email("valid@example.com");
        assertThat(email.value()).isEqualTo("valid@example.com");
    }

    @Test
    void should_throw_invalid_name_exception_when_blank() {
        assertThatThrownBy(() -> new Name("   "))
                .isInstanceOf(InvalidNameException.class)
                .hasMessageContaining("name");
    }

    @Test
    void should_throw_invalid_name_exception_when_too_long() {
        String longName = "a".repeat(51);
        assertThatThrownBy(() -> new Name(longName))
                .isInstanceOf(InvalidNameException.class)
                .hasMessageContaining("length");
    }

    @Test
    void should_create_valid_name() {
        Name name = new Name("John Doe");
        assertThat(name.value()).isEqualTo("John Doe");
    }

    @Test
    void should_create_profile_with_factory_method() {
        UUID id = UUID.randomUUID();
        ProfileId profileId = new ProfileId(id);
        Name name = new Name("Alice");
        Email email = new Email("alice@example.com");

        Profile profile = Profile.create(profileId, name, email);

        assertThat(profile.id()).isEqualTo(profileId);
        assertThat(profile.name()).isEqualTo(name);
        assertThat(profile.email()).isEqualTo(email);
    }

    @Test
    void should_update_profile_fields_and_keep_id_unchanged() {
        UUID initialId = UUID.randomUUID();
        ProfileId profileId = new ProfileId(initialId);
        Name oldName = new Name("Old Name");
        Email oldEmail = new Email("old@example.com");
        
        Profile originalProfile = Profile.create(profileId, oldName, oldEmail);

        Name newName = new Name("New Name");
        Email newEmail = new Email("new@example.com");

        Profile updatedProfile = originalProfile.update(newName, newEmail);

        // ID must remain unchanged
        assertThat(updatedProfile.id()).isEqualTo(originalProfile.id());
        assertThat(updatedProfile.id().id()).isEqualTo(initialId);

        // Fields must be updated
        assertThat(updatedProfile.name()).isEqualTo(newName);
        assertThat(updatedProfile.email()).isEqualTo(newEmail);
        
        // Original instance should not change (immutability)
        assertThat(originalProfile.name()).isEqualTo(oldName);
        assertThat(originalProfile.email()).isEqualTo(oldEmail);
    }
}

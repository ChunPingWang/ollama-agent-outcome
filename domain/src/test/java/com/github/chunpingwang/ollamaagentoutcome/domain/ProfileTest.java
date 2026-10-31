package com.github.chunpingwang.ollamaagentoutcome.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class ProfileTest {

    private final ProfileId profileId = new ProfileId(UUID.randomUUID());
    private final Email email = new Email("user@example.com");
    private final String name = "John Doe";

    @Test
    void should_create_profile_with_valid_data() {
        Profile profile = Profile.create(profileId, name, email);

        assertThat(profile.getId()).isEqualTo(profileId);
        assertThat(profile.getName()).isEqualTo(name);
        assertThat(profile.getEmail()).isEqualTo(email);
    }

    @Test
    void should_throw_when_name_is_blank() {
        assertThatThrownBy(() -> Profile.create(profileId, "   ", email))
                .isInstanceOf(InvalidNameException.class);
    }

    @Test
    void should_throw_when_name_is_null() {
        assertThatThrownBy(() -> Profile.create(profileId, null, email))
                .isInstanceOf(InvalidNameException.class);
    }

    @Test
    void should_update_name_successfully() {
        Profile profile = Profile.create(profileId, name, email);
        String newName = "Jane Doe";

        profile.updateName(newName);

        assertThat(profile.getName()).isEqualTo(newName);
    }

    @Test
    void should_throw_when_updating_name_to_blank() {
        Profile profile = Profile.create(profileId, name, email);

        assertThatThrownBy(() -> profile.updateName("   "))
                .isInstanceOf(InvalidNameException.class);
    }

    @Test
    void should_update_email_successfully() {
        Profile profile = Profile.create(profileId, name, email);
        Email newEmail = new Email("new@example.com");

        profile.updateEmail(newEmail);

        assertThat(profile.getEmail()).isEqualTo(newEmail);
    }

    @Test
    void should_throw_when_updating_email_to_invalid_format() {
        Profile profile = Profile.create(profileId, name, email);

        assertThatThrownBy(() -> profile.updateEmail(new Email("invalid")))
                .isInstanceOf(InvalidEmailException.class);
    }
}

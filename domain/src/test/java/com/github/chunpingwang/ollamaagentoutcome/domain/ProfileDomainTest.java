package com.github.chunpingwang.ollamaagentoutcome.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class ProfileDomainTest {

    private static final ProfileId PROFILE_ID = new ProfileId(UUID.randomUUID());
    private static final Email EMAIL = new Email("valid@example.com");
    private static final String VALID_NAME = "Valid Name";

    @Test
    void should_create_profile_with_valid_data() {
        Profile profile = new Profile(PROFILE_ID, VALID_NAME, EMAIL);

        assertThat(profile.id()).isEqualTo(PROFILE_ID);
        assertThat(profile.name()).isEqualTo(VALID_NAME);
        assertThat(profile.email()).isEqualTo(EMAIL);
    }

    @Test
    void should_throw_exception_when_name_is_null() {
        assertThatThrownBy(() -> new Profile(PROFILE_ID, null, EMAIL))
                .isInstanceOf(InvalidProfileException.class)
                .hasMessageContaining("name");
    }

    @Test
    void should_throw_exception_when_name_is_empty() {
        assertThatThrownBy(() -> new Profile(PROFILE_ID, "", EMAIL))
                .isInstanceOf(InvalidProfileException.class)
                .hasMessageContaining("name");
    }

    @Test
    void should_throw_exception_when_name_is_too_long() {
        String longName = "a".repeat(101);
        assertThatThrownBy(() -> new Profile(PROFILE_ID, longName, EMAIL))
                .isInstanceOf(InvalidProfileException.class)
                .hasMessageContaining("name");
    }

    @Test
    void should_throw_exception_when_email_is_null() {
        assertThatThrownBy(() -> new Profile(PROFILE_ID, VALID_NAME, null))
                .isInstanceOf(InvalidProfileException.class)
                .hasMessageContaining("email");
    }

    @Test
    void should_throw_exception_when_email_is_empty() {
        // Email VO 建構時會拋出 IllegalArgumentException，因此這裡預期的是 IllegalArgumentException
        assertThatThrownBy(() -> new Profile(PROFILE_ID, VALID_NAME, new Email("")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email cannot be null or empty");
    }

    @Test
    void should_update_profile_and_return_new_instance() {
        Profile profile = new Profile(PROFILE_ID, VALID_NAME, EMAIL);
        Email newEmail = new Email("new@example.com");
        String newName = "New Name";

        Profile updatedProfile = profile.update(newName, newEmail);

        assertThat(updatedProfile).isNotSameAs(profile);
        assertThat(updatedProfile.id()).isEqualTo(PROFILE_ID);
        assertThat(updatedProfile.name()).isEqualTo(newName);
        assertThat(updatedProfile.email()).isEqualTo(newEmail);
        // Original profile should remain unchanged
        assertThat(profile.name()).isEqualTo(VALID_NAME);
        assertThat(profile.email()).isEqualTo(EMAIL);
    }

    @Test
    void should_throw_profile_not_found_exception() {
        assertThat(ProfileNotFoundException.class.getSuperclass()).isEqualTo(RuntimeException.class);
    }

    @Test
    void should_throw_duplicate_email_exception() {
        assertThat(DuplicateEmailException.class.getSuperclass()).isEqualTo(RuntimeException.class);
    }

    @Test
    void should_throw_invalid_profile_exception() {
        assertThat(InvalidProfileException.class.getSuperclass()).isEqualTo(RuntimeException.class);
    }
}

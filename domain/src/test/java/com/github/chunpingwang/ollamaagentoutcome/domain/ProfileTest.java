package com.github.chunpingwang.ollamaagentoutcome.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class ProfileTest {

    @Test
    void should_create_profile_with_valid_data() {
        // given
        ProfileId id = new ProfileId(UUID.randomUUID());
        String name = "John Doe";
        String email = "john@example.com";
        String note = "A note";

        // when
        Profile profile = Profile.create(id, name, email, note);

        // then
        assertThat(profile.id()).isEqualTo(id);
        assertThat(profile.name()).isEqualTo(name);
        assertThat(profile.email()).isEqualTo(email);
        assertThat(profile.note()).isEqualTo(note);
    }

    @Test
    void should_create_profile_with_null_note() {
        // given
        ProfileId id = new ProfileId(UUID.randomUUID());
        String name = "John Doe";
        String email = "john@example.com";

        // when
        Profile profile = Profile.create(id, name, email, null);

        // then
        assertThat(profile.note()).isNull();
    }

    @Test
    void should_throw_when_name_is_blank() {
        // given
        ProfileId id = new ProfileId(UUID.randomUUID());
        String email = "john@example.com";

        // when & then
        assertThatThrownBy(() -> Profile.create(id, "   ", email, "note"))
                .isInstanceOf(InvalidNameException.class)
                .hasMessageContaining("name");
    }

    @Test
    void should_throw_when_name_is_null() {
        // given
        ProfileId id = new ProfileId(UUID.randomUUID());
        String email = "john@example.com";

        // when & then
        assertThatThrownBy(() -> Profile.create(id, null, email, "note"))
                .isInstanceOf(InvalidNameException.class)
                .hasMessageContaining("name");
    }

    @Test
    void should_throw_when_email_is_blank() {
        // given
        ProfileId id = new ProfileId(UUID.randomUUID());
        String name = "John Doe";

        // when & then
        assertThatThrownBy(() -> Profile.create(id, name, "   ", "note"))
                .isInstanceOf(InvalidEmailException.class)
                .hasMessageContaining("email");
    }

    @Test
    void should_throw_when_email_is_null() {
        // given
        ProfileId id = new ProfileId(UUID.randomUUID());
        String name = "John Doe";

        // when & then
        assertThatThrownBy(() -> Profile.create(id, name, null, "note"))
                .isInstanceOf(InvalidEmailException.class)
                .hasMessageContaining("email");
    }

    @Test
    void should_throw_when_email_format_is_invalid() {
        // given
        ProfileId id = new ProfileId(UUID.randomUUID());
        String name = "John Doe";

        // when & then
        assertThatThrownBy(() -> Profile.create(id, name, "invalid-email", "note"))
                .isInstanceOf(InvalidEmailException.class)
                .hasMessageContaining("email");
    }

    @Test
    void should_update_profile_with_valid_data() {
        // given
        ProfileId id = new ProfileId(UUID.randomUUID());
        Profile profile = Profile.create(id, "John Doe", "john@example.com", "old note");

        // when
        Profile updated = profile.update("Jane Doe", "jane@example.com", "new note");

        // then
        assertThat(updated.id()).isEqualTo(id);
        assertThat(updated.name()).isEqualTo("Jane Doe");
        assertThat(updated.email()).isEqualTo("jane@example.com");
        assertThat(updated.note()).isEqualTo("new note");
        // Ensure it's a new instance
        assertThat(updated).isNotSameAs(profile);
    }

    @Test
    void should_throw_when_update_name_is_blank() {
        // given
        ProfileId id = new ProfileId(UUID.randomUUID());
        Profile profile = Profile.create(id, "John Doe", "john@example.com", "note");

        // when & then
        assertThatThrownBy(() -> profile.update("   ", "jane@example.com", "note"))
                .isInstanceOf(InvalidNameException.class);
    }

    @Test
    void should_throw_when_update_email_is_invalid() {
        // given
        ProfileId id = new ProfileId(UUID.randomUUID());
        Profile profile = Profile.create(id, "John Doe", "john@example.com", "note");

        // when & then
        assertThatThrownBy(() -> profile.update("Jane Doe", "bad-email", "note"))
                .isInstanceOf(InvalidEmailException.class);
    }
}

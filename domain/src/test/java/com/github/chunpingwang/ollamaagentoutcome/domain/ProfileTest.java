package com.github.chunpingwang.ollamaagentoutcome.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class ProfileTest {

    @Test
    void should_create_profile_with_valid_data() {
        // given
        UUID id = UUID.randomUUID();
        Email email = new Email("test@example.com");
        Name name = new Name("Tester");

        // when
        Profile profile = new Profile(new ProfileId(id), name, email);

        // then
        assertThat(profile.id()).isEqualTo(new ProfileId(id));
        assertThat(profile.name()).isEqualTo(name);
        assertThat(profile.email()).isEqualTo(email);
    }

    @Test
    void should_update_name_successfully() {
        // given
        Profile profile = createValidProfile();

        // when
        Name newName = new Name("NewName");
        profile.updateName(newName);

        // then
        assertThat(profile.name()).isEqualTo(newName);
    }

    @Test
    void should_reject_null_or_blank_name_on_update() {
        // given
        Profile profile = createValidProfile();

        // expect
        assertThatThrownBy(() -> profile.updateName(null))
            .isInstanceOf(InvalidProfileException.class)
            .hasMessageContaining("name");
            
        assertThatThrownBy(() -> profile.updateName(new Name("   ")))
            .isInstanceOf(InvalidProfileException.class)
            .hasMessageContaining("name");
    }

    @Test
    void should_update_email_successfully() {
        // given
        Profile profile = createValidProfile();
        Email newEmail = new Email("new@example.com");

        // when
        profile.updateEmail(newEmail);

        // then
        assertThat(profile.email()).isEqualTo(newEmail);
    }

    private static Profile createValidProfile() {
        return new Profile(
            new ProfileId(UUID.randomUUID()), 
            new Name("OldName"), 
            new Email("old@example.com")
        );
    }
}

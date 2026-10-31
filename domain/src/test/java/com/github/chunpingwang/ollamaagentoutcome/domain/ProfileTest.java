package com.github.chunpingwang.ollamaagentoutcome.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class ProfileTest {

    @Test
    void should_create_profile_with_valid_data() {
        var id = new ProfileId(UUID.randomUUID());
        var email = new Email("test@example.com");
        var name = new DisplayName("John Doe");

        var profile = new Profile(id, email, name);

        assertThat(profile.id()).isEqualTo(id);
        assertThat(profile.email()).isEqualTo(email);
        assertThat(profile.displayName()).isEqualTo(name);
    }

    @Test
    void should_reject_null_id() {
        var email = new Email("test@example.com");
        var name = new DisplayName("Name");

        assertThatThrownBy(() -> new Profile(null, email, name))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("id must not be null");
    }

    @Test
    void should_update_display_name() {
        var id = new ProfileId(UUID.randomUUID());
        var email = new Email("test@example.com");
        var oldName = new DisplayName("Old Name");
        
        var profile = new Profile(id, email, oldName);
        
        var newName = new DisplayName("New Name");
        var updatedProfile = profile.changeDisplayName(newName);

        assertThat(updatedProfile).isSameAs(profile); // Returns same instance with updated state
        assertThat(updatedProfile.displayName()).isEqualTo(newName);
    }
}

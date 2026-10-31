package com.github.chunpingwang.ollamaagentoutcome.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import com.github.chunpingwang.ollamaagentoutcome.domain.exception.BioTooLongException;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Bio;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.ProfileId;

import org.junit.jupiter.api.Test;

class ProfileTest {

    @Test
    void should_create_profile_with_valid_data() {
        var id = new ProfileId(UUID.randomUUID());
        var email = new Email("test@example.com");
        var displayName = new DisplayName("John Doe");
        var bio = new Bio("Hello World");

        var profile = new Profile(id, email, displayName, bio);

        assertThat(profile.getId()).isEqualTo(id);
        assertThat(profile.getEmail().address()).isEqualTo(email.address());
        assertThat(profile.getDisplayName().name()).isEqualTo(displayName.name());
        assertThat(profile.getBio().content()).isEqualTo(bio.content());
    }

    @Test
    void should_allow_null_display_name_on_creation() {
        var id = new ProfileId(UUID.randomUUID());
        var email = new Email("test@example.com");
        
        // Should not throw if display name is null (handled by entity)
        var profile = new Profile(id, email, null, null);
        
        assertThat(profile.getDisplayName().name()).isNull();
    }

    @Test
    void should_change_display_name_successfully() {
        var id = new ProfileId(UUID.randomUUID());
        var email = new Email("test@example.com");
        var initialBio = new Bio("");
        var profile = new Profile(id, email, new DisplayName("Old"), initialBio);

        profile.changeDisplayName(new DisplayName("New Name"));

        assertThat(profile.getDisplayName().name()).isEqualTo("New Name");
    }

    @Test
    void should_reject_blank_display_name_update() {
        var id = new ProfileId(UUID.randomUUID());
        var email = new Email("test@example.com");
        var profile = new Profile(id, email, new DisplayName("Old"), new Bio(""));

        assertThatThrownBy(() -> profile.changeDisplayName(new DisplayName("   ")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be blank");
    }

    @Test
    void should_change_bio_successfully() {
        var id = new ProfileId(UUID.randomUUID());
        var email = new Email("test@example.com");
        var bio1 = new Bio("Initial Bio");
        var profile = new Profile(id, email, null, bio1);

        var bio2 = new Bio("Updated Bio");
        profile.changeBio(bio2);

        assertThat(profile.getBio().content()).isEqualTo("Updated Bio");
    }
}

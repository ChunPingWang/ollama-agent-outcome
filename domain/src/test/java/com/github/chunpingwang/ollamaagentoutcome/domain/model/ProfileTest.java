package com.github.chunpingwang.ollamaagentoutcome.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Bio;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Email;

class ProfileTest {

    private final ProfileId profileId = new ProfileId(UUID.randomUUID());
    private final Email email = new Email("test@example.com");
    private final DisplayName displayName = new DisplayName("Test User");
    private final Bio bio = new Bio("Hello");

    @Test
    void should_create_profile_with_valid_data() {
        var profile = new Profile(profileId, email, displayName, bio);
        
        assertThat(profile.id()).isEqualTo(profileId);
        assertThat(profile.email()).isEqualTo(email);
        assertThat(profile.displayName()).isEqualTo(displayName);
        assertThat(profile.bio()).isEqualTo(bio);
    }

    @Test
    void should_update_profile_successfully() {
        var profile = new Profile(profileId, email, displayName, bio);
        
        var newEmail = new Email("updated@example.com");
        var newDisplayName = new DisplayName("Updated Name");
        var newBio = new Bio("New Bio Text");

        profile.update(newEmail, newDisplayName, newBio);

        assertThat(profile.email()).isEqualTo(newEmail);
        assertThat(profile.displayName()).isEqualTo(newDisplayName);
        assertThat(profile.bio()).isEqualTo(newBio);
    }

    @Test
    void should_reject_invalid_email_on_update() {
        var profile = new Profile(profileId, email, displayName, bio);
        
        assertThatThrownBy(() -> profile.update(
                new Email("invalid-email"), 
                displayName, 
                bio))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_reject_invalid_display_name_on_update() {
        var profile = new Profile(profileId, email, displayName, bio);
        
        String longName = "A".repeat(101);
        assertThatThrownBy(() -> profile.update(
                email, 
                new DisplayName(longName), 
                bio))
                .isInstanceOf(IllegalArgumentException.class);
    }
}

package com.github.chunpingwang.ollamaagentoutcome.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Bio;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.ProfileId;

class ProfileTest {

    private final UUID id = UUID.randomUUID();
    private final Email email = new Email("test@example.com");
    private final DisplayName displayName = new DisplayName("Original Name");
    private final Bio bio = new Bio("Original Bio");

    @Test
    void should_create_profile_with_all_fields() {
        Profile profile = new Profile(new ProfileId(id), email, displayName, bio);

        assertThat(profile.id().value()).isEqualTo(id);
        assertThat(profile.email()).isEqualTo(email);
        assertThat(profile.displayName()).isEqualTo(displayName);
        assertThat(profile.bio()).isEqualTo(bio);
    }

    @Test
    void should_update_display_name_and_bio_successfully() {
        Profile profile = new Profile(new ProfileId(id), email, displayName, bio);
        
        DisplayName newName = new DisplayName("New Name");
        Bio newBio = new Bio("Updated Bio");

        profile.update(newName, newBio);

        assertThat(profile.displayName().name()).isEqualTo("New Name");
        assertThat(profile.bio().content()).isEqualTo("Updated Bio");
        // ID and Email should remain unchanged
        assertThat(profile.id().value()).isEqualTo(id);
        assertThat(profile.email().address()).isEqualTo("test@example.com");
    }
}

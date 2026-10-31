package com.github.chunpingwang.ollamaagentoutcome.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class ProfileTest {

    private final UUID idValue = UUID.randomUUID();
    private final Email email = new Email("test@example.com");
    private final DisplayName displayName = new DisplayName("Tester");
    private final Bio bio = new Bio("Hello World");

    @Test
    void should_create_profile_with_valid_data() {
        Profile profile = new Profile(new ProfileId(idValue), email, displayName, bio);
        
        assertThat(profile.id().value()).isEqualTo(idValue);
        assertThat(profile.email().address()).isEqualTo("test@example.com");
        assertThat(profile.displayName().name()).contains("Tester");
        assertThat(profile.bio().content()).isEqualTo("Hello World");
    }

    @Test
    void should_update_display_name_and_bio() {
        Profile profile = new Profile(new ProfileId(idValue), email, displayName, bio);
        
        DisplayName newName = new DisplayName("New Name");
        Bio newBio = new Bio("Updated Bio");
        
        profile.updateDisplayNameAndBio(newName, newBio);
        
        assertThat(profile.displayName().name()).contains("New Name");
        assertThat(profile.bio().content()).isEqualTo("Updated Bio");
        // 其他欄位應保持不變
        assertThat(profile.email().address()).isEqualTo("test@example.com");
    }

    @Test
    void should_not_allow_null_id() {
        assertThatThrownBy(() -> new Profile(null, email, displayName, bio))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("id must not be null");
    }
}

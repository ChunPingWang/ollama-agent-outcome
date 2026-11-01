package com.github.chunpingwang.ollamaagentoutcome.domain;


import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class DomainEntityTest {

    private final UUID testId = UUID.randomUUID();
    private final ProfileId profileId = new ProfileId(testId);
    private final Email validEmail = new Email("test@example.com");
    private final DisplayName displayName = new DisplayName("Test User");

    @Test
    void should_create_profile_with_valid_data() {
        Profile profile = new Profile(profileId, validEmail, displayName);
        
        assertThat(profile.getId()).isEqualTo(profileId);
        assertThat(profile.getEmail().getValue()).isEqualTo("test@example.com");
        assertThat(profile.getDisplayName().getValue()).isEqualTo("Test User");
    }

    @Test
    void should_throw_when_creating_profile_with_null_id() {
        assertThatThrownBy(() -> new Profile(null, validEmail, displayName))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("ID cannot be null");
    }

    @Test
    void should_update_display_name_successfully() {
        Profile profile = new Profile(profileId, validEmail, displayName);
        
        DisplayName newName = new DisplayName("New Name");
        profile.setDisplayName(newName);
        
        assertThat(profile.getDisplayName().getValue()).isEqualTo("New Name");
    }

    @Test
    void should_allow_empty_display_name_on_update() {
        Profile profile = new Profile(profileId, validEmail, displayName);
        
        profile.setDisplayName(new DisplayName(""));
        
        assertThat(profile.getDisplayName().getValue()).isEmpty();
    }

    @Test
    void should_reject_pure_whitespace_display_name_on_update() {
        Profile profile = new Profile(profileId, validEmail, displayName);
        
        assertThatThrownBy(() -> profile.setDisplayName(new DisplayName("   ")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("whitespace");
    }

    @Test
    void should_not_alter_email_when_updating_display_name() {
        Profile profile = new Profile(profileId, validEmail, displayName);
        
        profile.setDisplayName(new DisplayName("Updated"));
        
        assertThat(profile.getEmail().getValue()).isEqualTo("test@example.com");
    }
}

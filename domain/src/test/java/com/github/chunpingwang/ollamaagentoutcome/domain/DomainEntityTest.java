package com.github.chunpingwang.ollamaagentoutcome.domain;


import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class DomainEntityTest {

    private final ProfileId profileId = new ProfileId(UUID.randomUUID());
    private final Email email = new Email("test@example.com");
    private final DisplayName displayName = new DisplayName("Test User");

    @Test
    void should_create_profile_with_valid_data() {
        Profile profile = new Profile(profileId, email, displayName);
        
        assertThat(profile.getId()).isEqualTo(profileId);
        assertThat(profile.getEmail().value()).isEqualTo(email.value());
        assertThat(profile.getDisplayName().value()).isEqualTo(displayName.value());
    }

    @Test
    void should_throw_when_email_is_null_in_constructor() {
        assertThatThrownBy(() -> new Profile(profileId, null, displayName))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("email");
    }

    @Test
    void should_update_display_name_successfully() {
        Profile profile = new Profile(profileId, email, displayName);
        
        DisplayName newName = new DisplayName("New Name");
        profile.updateDisplayName(newName);
        
        assertThat(profile.getDisplayName().value()).isEqualTo("New Name");
    }

    @Test
    void should_throw_when_updating_to_null_display_name() {
        Profile profile = new Profile(profileId, email, displayName);
        
        // updateDisplayName usually requires non-null for the argument in my planned impl
        // or throws if trying to set it back to unset? 
        // Let's assume we can't pass null directly based on typical strict validation.
        assertThatThrownBy(() -> profile.updateDisplayName(null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}

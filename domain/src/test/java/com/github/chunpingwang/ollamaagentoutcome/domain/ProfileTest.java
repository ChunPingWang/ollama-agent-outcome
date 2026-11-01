package com.github.chunpingwang.ollamaagentoutcome.domain;


import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class ProfileTest {

    private final String validId = UUID.randomUUID().toString();
    private final Email email = new Email("test@example.com");
    private final DisplayName displayName = new DisplayName("Test User");

    @Test
    void should_create_profile_with_valid_data() {
        Profile profile = Profile.create(new ProfileId(validId), email, displayName);
        
        assertThat(profile.id()).isEqualTo(new ProfileId(validId));
        assertThat(profile.email()).isEqualTo(email);
        assertThat(profile.displayName()).isEqualTo(displayName);
    }

    @Test
    void should_update_display_name_and_email() {
        Profile profile = Profile.create(new ProfileId(validId), email, displayName);
        
        Email newEmail = new Email("new@example.com");
        DisplayName newName = new DisplayName("New Name");
        
        profile.update(newEmail, newName);
        
        assertThat(profile.email()).isEqualTo(newEmail);
        assertThat(profile.displayName()).isEqualTo(newName);
        // ID and creation time (if any) should remain unchanged
        assertThat(profile.id()).isEqualTo(new ProfileId(validId));
    }
    
    @Test
    void should_throw_if_invalid_id_provided_to_create() {
         assertThatThrownBy(() -> Profile.create(new ProfileId("invalid-uuid"), email, displayName))
            .isInstanceOf(IllegalArgumentException.class);
    }
}

package com.github.chunpingwang.ollamaagentoutcome.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.ProfileId;

class ProfileTest {

    @Test
    void should_create_profile_with_valid_data() {
        var id = UUID.randomUUID();
        var email = new Email("test@example.com");
        var displayName = new DisplayName("Test User");

        var profile = new Profile(new ProfileId(id), email, displayName);

        assertThat(profile.id()).isEqualTo(new ProfileId(id));
        assertThat(profile.email()).isEqualTo(email);
        assertThat(profile.displayName()).isEqualTo(displayName);
    }

    @Test
    void should_throw_when_creating_profile_with_invalid_email() {
        var id = UUID.randomUUID();
        
        // Creating an invalid Email throws IllegalArgumentException before Profile is constructed
        assertThatThrownBy(() -> new Profile(new ProfileId(id), new Email("invalid"), null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid");
    }
    
    @Test
    void should_update_display_name() {
        var id = UUID.randomUUID();
        var email = new Email("update@test.com");
        var oldDisplayName = new DisplayName("Old Name");
        var profile = new Profile(new ProfileId(id), email, oldDisplayName);

        var newDisplayName = new DisplayName("New Name");
        profile.updateDisplayName(newDisplayName);

        assertThat(profile.displayName().value()).isEqualTo("New Name");
        assertThat(profile.email().value()).isEqualTo("update@test.com");
    }
}

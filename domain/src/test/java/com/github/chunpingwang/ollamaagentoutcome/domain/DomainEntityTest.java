package com.github.chunpingwang.ollamaagentoutcome.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class DomainEntityTest {

    private final UUID testId = UUID.randomUUID();
    
    @Test
    void should_create_profile_with_valid_data() {
        Email email = new Email("test@example.com");
        DisplayName name = new DisplayName("Test User");
        
        Profile profile = new Profile(new ProfileId(testId), email, name);
        
        assertThat(profile.getId().id()).isEqualTo(testId);
        assertThat(profile.getEmail().getValue()).isEqualTo("test@example.com");
        assertThat(profile.getDisplayName().value()).isEqualTo("Test User");
    }

    @Test
    void should_throw_when_creating_profile_without_email() {
        assertThatThrownBy(() -> 
            new Profile(new ProfileId(UUID.randomUUID()), null, new DisplayName("User"))
        ).isInstanceOf(NullPointerException.class)
         .hasMessageContaining("email must not be null for creation");
    }

    @Test
    void should_update_display_name() {
        Email email = new Email("test@example.com");
        DisplayName oldName = new DisplayName("Old Name");
        Profile profile = new Profile(new ProfileId(testId), email, oldName);
        
        DisplayName newName = new DisplayName("New Name");
        profile.updateDisplayName(newName);
        
        assertThat(profile.getDisplayName().value()).isEqualTo("New Name");
    }
    
    @Test
    void should_create_static_profile_with_uuid() {
        UUID uuid = UUID.randomUUID();
        String emailStr = "static@test.com";
        String displayNameStr = "Static User";
        
        Profile profile = Profile.create(uuid, emailStr, displayNameStr);
        
        assertThat(profile.getId().id()).isEqualTo(uuid);
        assertThat(profile.getEmail().getValue()).isEqualTo(emailStr.toLowerCase());
        assertThat(profile.getDisplayName().value()).isEqualTo(displayNameStr);
    }
}

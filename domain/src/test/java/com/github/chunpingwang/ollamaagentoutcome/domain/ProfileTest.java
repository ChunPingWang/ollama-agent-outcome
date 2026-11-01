package com.github.chunpingwang.ollamaagentoutcome.domain;


import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class ProfileTest {

    private final UUID id = UUID.randomUUID();
    private final Email email = new Email("test@example.com");
    private final DisplayName displayName = new DisplayName("Old Name");

    @Test
    void should_create_profile_with_initial_values() {
        Profile profile = new Profile(id, email, displayName);
        
        assertThat(profile.id()).isEqualTo(new ProfileId(id.toString()));
        assertThat(profile.email()).isEqualTo(email);
        assertThat(profile.displayName()).isEqualTo(displayName);
    }

    @Test
    void should_update_display_name() {
        Profile profile = new Profile(id, email, displayName);
        DisplayName newName = new DisplayName("New Name");
        
        profile.updateDisplayName(newName);
        
        assertThat(profile.displayName().name()).isEqualTo("New Name");
    }
    
    @Test
    void should_allow_clearing_display_name() {
        Profile profile = new Profile(id, email, displayName);
        
        profile.updateDisplayName(null); // 或空值物件
        
        assertThat(profile.displayName()).isNull();
    }
}

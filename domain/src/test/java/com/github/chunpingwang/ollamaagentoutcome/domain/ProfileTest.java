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
        Profile profile = new Profile(validId, email, displayName);
        
        assertThat(profile.id()).isEqualTo(validId);
        assertThat(profile.email()).isEqualTo(email);
        assertThat(profile.displayName()).isEqualTo(displayName);
    }

    @Test
    void should_change_display_name_successfully() {
        Profile profile = new Profile(validId, email, displayName);
        DisplayName newName = new DisplayName("New Name");
        
        Profile updated = profile.changeDisplayName(newName);
        
        // Should return a new instance or the same one if unchanged? 
        // Usually immutable pattern returns new, mutable updates self and returns this.
        // Let's assume it returns the updated profile (could be 'this' or new).
        // The test verifies state change regardless of identity.
        assertThat(updated.displayName()).isEqualTo(newName);
        assertThat(updated.email()).isEqualTo(email); // Unchanged
    }

    @Test
    void should_reject_blank_new_display_name() {
        Profile profile = new Profile(validId, email, displayName);
        
        assertThatThrownBy(() -> profile.changeDisplayName(new DisplayName("   ")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("不能為空白");
    }

    @Test
    void should_change_email_successfully() {
        Profile profile = new Profile(validId, email, displayName);
        Email newEmail = new Email("new@example.com");
        
        Profile updated = profile.changeEmail(newEmail);
        
        assertThat(updated.email()).isEqualTo(newEmail);
        assertThat(updated.displayName()).isEqualTo(displayName); // Unchanged
    }

    @Test
    void should_reject_invalid_new_email_format() {
        Profile profile = new Profile(validId, email, displayName);
        
        assertThatThrownBy(() -> profile.changeEmail(new Email("invalid")))
                .isInstanceOf(InvalidEmailException.class);
    }
}

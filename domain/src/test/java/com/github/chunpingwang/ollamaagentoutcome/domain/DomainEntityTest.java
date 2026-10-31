package com.github.chunpingwang.ollamaagentoutcome.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class DomainEntityTest {

    private final ProfileId id = new ProfileId(UUID.randomUUID());
    private final Email email = new Email("test@example.com");
    private final DisplayName displayName = new DisplayName("Test User");

    @Test
    void should_create_profile_with_valid_data() {
        Profile profile = new Profile(id, email, displayName);
        
        assertThat(profile.getId()).isEqualTo(id);
        assertThat(profile.getEmail().getValue()).isEqualTo(email.getValue());
        assertThat(profile.getDisplayName().getValue()).isEqualTo(displayName.getValue());
    }

    @Test
    void should_reject_null_id_on_creation() {
        assertThatThrownBy(() -> new Profile(null, email, displayName))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("ID cannot be null");
    }

    @Test
    void should_reject_null_email_on_creation() {
        assertThatThrownBy(() -> new Profile(id, null, displayName))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Email cannot be null");
    }

    @Test
    void should_allow_null_display_name_value_in_profile() {
        DisplayName nullName = new DisplayName((String) null);
        Profile profile = new Profile(id, email, nullName);
        
        assertThat(profile.getDisplayName().getValue()).isNull();
    }

    @Test
    void should_update_display_name_successfully() {
        Profile profile = new Profile(id, email, displayName);
        
        DisplayName newName = new DisplayName("New User");
        profile.updateDisplayName(newName);
        
        assertThat(profile.getDisplayName().getValue()).isEqualTo("New User");
    }

    @Test
    void should_reject_null_new_display_name_on_update() {
        Profile profile = new Profile(id, email, displayName);
        
        assertThatThrownBy(() -> profile.updateDisplayName(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("display name cannot be null");
    }
}

package com.github.chunpingwang.ollamaagentoutcome.domain;


import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class ProfileTest {

    private final ProfileId id = new ProfileId(UUID.randomUUID().toString());
    private final Email email = new Email("test@example.com");
    private final DisplayName displayName = new DisplayName("Test User");

    @Test
    void should_create_profile_with_valid_data() {
        Profile profile = Profile.create(id, email, displayName);
        
        assertThat(profile.getId()).isEqualTo(id);
        assertThat(profile.getEmail()).isEqualTo(email);
        assertThat(profile.getDisplayName()).isEqualTo("Test User");
    }

    @Test
    void should_reject_null_id_on_creation() {
        assertThatThrownBy(() -> Profile.create(null, email, displayName))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void should_reject_null_email_on_creation() {
        assertThatThrownBy(() -> Profile.create(id, null, displayName))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Email cannot be null");
    }

    @Test
    void should_allow_null_display_name_on_creation() {
        Profile profile = Profile.create(id, email, null);
        assertThat(profile.getDisplayName()).isNull();
    }

    @Test
    void should_update_email() {
        Profile profile = Profile.create(id, email, displayName);
        Email newEmail = new Email("new@example.com");
        
        profile.setEmail(newEmail);
        
        assertThat(profile.getEmail()).isEqualTo(newEmail);
    }

    @Test
    void should_reject_null_email_on_update() {
        Profile profile = Profile.create(id, email, displayName);
        
        assertThatThrownBy(() -> profile.setEmail(null))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_update_display_name() {
        Profile profile = Profile.create(id, email, displayName);
        DisplayName newName = new DisplayName("New Name");
        
        profile.setDisplayName(newName);
        
        assertThat(profile.getDisplayName()).isEqualTo("New Name");
    }

    @Test
    void should_equality_based_on_id() {
        Profile p1 = Profile.create(id, email, displayName);
        Profile p2 = Profile.create(id, new Email("other@test.com"), new DisplayName("Other"));
        
        assertThat(p1).isEqualTo(p2);
        assertThat(p1.hashCode()).isEqualTo(p2.hashCode());
    }

    @Test
    void should_not_equal_different_ids() {
        ProfileId id2 = new ProfileId(UUID.randomUUID().toString());
        Profile p1 = Profile.create(id, email, displayName);
        Profile p2 = Profile.create(id2, email, displayName);
        
        assertThat(p1).isNotEqualTo(p2);
    }
}

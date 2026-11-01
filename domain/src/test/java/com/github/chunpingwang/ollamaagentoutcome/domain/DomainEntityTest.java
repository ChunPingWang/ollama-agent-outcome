package com.github.chunpingwang.ollamaagentoutcome.domain;


import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class DomainEntityTest {

    private final UUID testId = UUID.randomUUID();
    
    @Test
    void should_create_profile_with_valid_data() {
        Profile profile = newProfile(testId, "user@example.com", "Display Name");
        
        assertThat(profile.getId().id()).isEqualTo(testId);
        assertThat(profile.getEmailValue()).isEqualTo("user@example.com");
        assertThat(profile.getDisplayNameValue()).isEqualTo("Display Name");
    }

    @Test
    void should_reject_null_email_on_creation() {
        assertThatThrownBy(() -> new Profile(
                new ProfileId(testId), 
                null, 
                new DisplayName("Name")
        )).isInstanceOf(NullPointerException.class)
          .hasMessageContaining("email cannot be null");
    }

    @Test
    void should_update_display_name_successfully() {
        Profile profile = newProfile(testId, "user@example.com", "Old Name");
        
        profile.updateDisplayName(new DisplayName("New Name"));
        
        assertThat(profile.getDisplayNameValue()).isEqualTo("New Name");
    }

    @Test
    void should_clear_display_name_when_set_to_blank() {
        Profile profile = newProfile(testId, "user@example.com", "Old Name");
        
        profile.updateDisplayName(new DisplayName(null));
        
        assertThat(profile.getDisplayNameValue()).isEmpty();
    }

    @Test
    void should_have_same_email_method_works() {
        Profile profile = newProfile(testId, "test@test.com", "Name");
        
        assertThat(profile.hasSameEmail("test@test.com")).isTrue();
        assertThat(profile.hasSameEmail("other@test.com")).isFalse();
    }

    private Profile newProfile(UUID id, String email, String displayName) {
        return new Profile(
            new ProfileId(id),
            new Email(email),
            new DisplayName(displayName)
        );
    }
}

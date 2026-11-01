package com.github.chunpingwang.ollamaagentoutcome.domain;





import static org.assertj.core.api.Assertions.*;import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class ProfileTest {

    private final String rawId = UUID.randomUUID().toString();
    private final Email email = new Email("test@example.com");
    private final DisplayName displayName = new DisplayName("Test User");

    @Test
    void should_create_profile_successfully() {
        ProfileId id = new ProfileId(rawId);
        Profile profile = Profile.create(id, email, displayName);

        assertThat(profile.id()).isEqualTo(new ProfileId(rawId));
        assertThat(profile.email()).isEqualTo(email);
        assertThat(profile.displayName()).isEqualTo(displayName);
    }

    @Test
    void should_fail_to_update_with_duplicate_email_logic_in_domain() {
        
        ProfileId id = new ProfileId(rawId);
        Profile profile = Profile.create(id, email, displayName);
        Email newEmail = new Email("updated@example.com");
        DisplayName newName = new DisplayName("Updated Name");

        Profile updatedProfile = profile.update(newEmail, newName);

        assertThat(updatedProfile).isNotSameAs(profile);
        assertThat(updatedProfile.email()).isEqualTo(newEmail);
        assertThat(updatedProfile.displayName()).isEqualTo(newName);
    }
    
    @Test
    void should_not_allow_null_id_on_creation() {
         assertThatThrownBy(() -> Profile.create(null, email, displayName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("id");
    }
}

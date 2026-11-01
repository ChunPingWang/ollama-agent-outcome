package com.github.chunpingwang.ollamaagentoutcome.domain;



import static org.assertj.core.api.Assertions.*;import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class ProfileTest {

    private final String validId = UUID.randomUUID().toString();
    private final Email email = new Email("user@example.com");
    private final DisplayName displayName = new DisplayName("John Doe");

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
        
        Email updatedEmail = new Email("newemail@example.com");
        DisplayName updatedDisplayName = new DisplayName("Jane Doe");
        
        profile.update(updatedEmail, updatedDisplayName);
        
        assertThat(profile.email()).isEqualTo(updatedEmail);
        assertThat(profile.displayName()).isEqualTo(updatedDisplayName);
    }

    @Test
    void should_throw_exception_when_creating_with_invalid_id_format() {
        assertThatThrownBy(() -> Profile.create(new ProfileId("invalid-uuid"), email, displayName))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("invalid");
    }
}

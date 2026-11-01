package com.github.chunpingwang.ollamaagentoutcome.domain;


import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class ProfileTest {

    private final UUID id = UUID.randomUUID();
    private final Email email = new Email("test@example.com");
    private final DisplayName displayName = new DisplayName("Test User");

    @Test
    void should_create_profile_with_valid_data() {
        Profile profile = Profile.create(new ProfileId(id.toString()), email, displayName);
        
        assertThat(profile.id().value()).isEqualTo(id.toString());
        assertThat(profile.email().address()).isEqualTo(email.address());
        assertThat(profile.displayName().name()).isEqualTo(displayName.name());
    }

    @Test
    void should_throw_when_creating_with_invalid_email_format() {
        // Email constructor throws InvalidEmailFormatException before create is called
        assertThatThrownBy(() -> new Email("invalid-email"))
                .isInstanceOf(InvalidEmailFormatException.class)
                .hasMessageContaining("email");
    }

    @Test
    void should_update_display_name_and_email() {
        Profile profile = Profile.create(new ProfileId(id.toString()), email, displayName);
        
        Email newEmail = new Email("new@example.com");
        DisplayName newName = new DisplayName("New Name");
        
        profile.update(newEmail, newName);
        
        assertThat(profile.email().address()).isEqualTo(newEmail.address());
        assertThat(profile.displayName().name()).isEqualTo(newName.name());
    }
    
    @Test
    void should_not_allow_null_id_in_factory() {
         assertThatThrownBy(() -> Profile.create(null, email, displayName))
                 .isInstanceOf(IllegalArgumentException.class)
                 .hasMessageContaining("id must not be null");
    }
}

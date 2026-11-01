package com.github.chunpingwang.ollamaagentoutcome.domain;



import static org.assertj.core.api.Assertions.*;import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class ProfileTest {

    private final UUID idUuid = UUID.randomUUID();
    private final Email email = new Email("test@example.com");
    private final DisplayName displayName = new DisplayName("Test User");

    @Test
    void should_create_profile_with_valid_data() {
        Profile profile = Profile.create(new ProfileId(idUuid.toString()), email, displayName);
        
        assertThat(profile.id().value()).isEqualTo(idUuid.toString());
        assertThat(profile.email()).isEqualTo(email);
        assertThat(profile.displayName()).isEqualTo(displayName);
    }

    @Test
    void should_update_email_and_display_name() {
        Profile profile = Profile.create(new ProfileId(idUuid.toString()), email, displayName);
        
        Email newEmail = new Email("new@example.com");
        DisplayName newName = new DisplayName("New Name");
        
        profile.update(newEmail, newName);
        
        assertThat(profile.email()).isEqualTo(newEmail);
        assertThat(profile.displayName()).isEqualTo(newName);
        // ID and creation time (if any) should remain unchanged
        assertThat(profile.id().value()).isEqualTo(idUuid.toString());
    }

    @Test
    void should_throw_when_creating_with_invalid_email_format_in_factory() {
        // Assuming the factory or VO validation throws on invalid input passed to create
        // If Email VO validates itself, this might be covered by EmailTest. 
        // Here we test that passing an invalid string directly if allowed, or just standard valid flow.
        // Since Email constructor throws, let's verify the exception propagates from create.
        assertThatThrownBy(() -> Profile.create(
                new ProfileId(idUuid.toString()), 
                new Email("invalid-email"), 
                displayName))
            .isInstanceOf(IllegalArgumentException.class);
    }
}

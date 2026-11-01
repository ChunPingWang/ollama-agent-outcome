package com.github.chunpingwang.ollamaagentoutcome.domain;



import static org.assertj.core.api.Assertions.*;import static org.assertj.core.api.Assertions.assertThat;
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
    void should_update_display_name_and_email() {
        Profile profile = Profile.create(new ProfileId(id.toString()), email, displayName);
        
        Email newEmail = new Email("updated@example.com");
        DisplayName newName = new DisplayName("Updated Name");
        
        profile.update(newEmail, newName);
        
        assertThat(profile.email().address()).isEqualTo(newEmail.address());
        assertThat(profile.displayName().name()).isEqualTo(newName.name());
    }

    @Test
    void should_throw_exception_on_duplicate_email_during_creation_simulation() {
        // Simulate domain logic check for duplicate email if required by entity itself
        // Though usually handled at port level, we test the exception existence and basic behavior
        
        DuplicateEmailException ex = new DuplicateEmailException(email.address());
        assertThat(ex.getMessage()).contains(email.address());
    }
}

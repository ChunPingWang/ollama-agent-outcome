package com.github.chunpingwang.ollamaagentoutcome.domain;


import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProfileTest {

    private final UUID testId = UUID.randomUUID();
    private final String validEmail = "test@example.com";
    private final String validDisplayName = "Tester";

    @Test
    void should_create_profile_with_all_fields() {
        Profile profile = Profile.create(testId, validEmail, validDisplayName);

        assertThat(profile.getIdValue()).isEqualTo(testId);
        assertThat(profile.getEmailString()).isEqualTo(validEmail.toLowerCase());
        assertThat(profile.getDisplayNameString()).isEqualTo(validDisplayName);
    }

    @Test
    void should_update_email_and_display_name() {
        Profile profile = Profile.create(testId, validEmail, validDisplayName);
        
        String newEmail = "new@test.com";
        String newName = "New Name";
        
        profile.update(newEmail, newName);
        
        assertThat(profile.getEmailString()).isEqualTo(newEmail.toLowerCase());
        assertThat(profile.getDisplayNameString()).isEqualTo(newName);
    }

    @Test
    void should_throw_invalid_email_when_updating_to_bad_format() {
        Profile profile = Profile.create(testId, validEmail, validDisplayName);
        
        assertThatThrownBy(() -> profile.update("invalid-email", "New"))
                .isInstanceOf(InvalidEmailException.class);
    }
    
     @Test
    void should_handle_null_display_name_on_creation() {
         Profile profile = Profile.create(testId, validEmail, null);
         assertThat(profile.getDisplayNameString()).isEmpty();
    }
}

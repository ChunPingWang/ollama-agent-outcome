package com.github.chunpingwang.ollamaagentoutcome.domain;


import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class ProfileTest {

    @Test
    void should_create_profile_with_valid_data() {
        String idStr = UUID.randomUUID().toString();
        Email email = new Email("test@example.com");
        DisplayName displayName = new DisplayName("Tester");
        
        Profile profile = Profile.create(new ProfileId(idStr), email, displayName);
        
        assertThat(profile.id()).isEqualTo(new ProfileId(idStr));
        assertThat(profile.email()).isEqualTo(email);
        assertThat(profile.displayName()).isEqualTo(displayName);
    }

    @Test
    void should_update_email_and_display_name() {
        String idStr = UUID.randomUUID().toString();
        Email initialEmail = new Email("old@test.com");
        DisplayName initialName = new DisplayName("Old Name");
        
        Profile profile = Profile.create(new ProfileId(idStr), initialEmail, initialName);
        
        Email newEmail = new Email("new@test.com");
        DisplayName newName = new DisplayName("New Name");
        
        profile.update(newEmail, newName);
        
        assertThat(profile.email()).isEqualTo(newEmail);
        assertThat(profile.displayName()).isEqualTo(newName);
        // ID 不應改變
        assertThat(profile.id()).isEqualTo(new ProfileId(idStr));
    }

    @Test
    void should_throw_when_creating_with_invalid_id_format() {
        assertThatThrownBy(() -> 
            Profile.create(new ProfileId("bad-id"), new Email("a@b.c"), new DisplayName("N")))
                .isInstanceOf(IllegalArgumentException.class);
    }
}

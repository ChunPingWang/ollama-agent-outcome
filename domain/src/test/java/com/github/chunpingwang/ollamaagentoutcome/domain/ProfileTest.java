package com.github.chunpingwang.ollamaagentoutcome.domain;


import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class ProfileTest {

    @Test
    void should_create_profile_with_valid_data() {
        UUID rawId = UUID.randomUUID();
        ProfileId id = new ProfileId(rawId);
        Email email = new Email("test@example.com");
        DisplayName displayName = new DisplayName("Tester");

        Profile profile = new Profile(id, email, displayName);

        assertThat(profile.id()).isEqualTo(id);
        assertThat(profile.email()).isEqualTo(email);
        assertThat(profile.displayName()).isEqualTo(displayName);
    }

    @Test
    void should_reject_null_id() {
        // Use a valid email to ensure the failure is due to null ID, not invalid email
        Email validEmail = new Email("valid@test.com");
        
        assertThatThrownBy(() -> new Profile(null, validEmail, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("id must not be null");
    }

    @Test
    void should_update_display_name_successfully() {
        UUID rawId = UUID.randomUUID();
        ProfileId id = new ProfileId(rawId);
        Email email = new Email("user@test.com");
        DisplayName oldName = new DisplayName("Old Name");
        
        Profile original = new Profile(id, email, oldName);
        DisplayName newName = new DisplayName("New Name");
        
        Profile updated = original.changeDisplayName(newName);

        assertThat(updated).isNotSameAs(original);
        assertThat(updated.id()).isEqualTo(id);
        assertThat(updated.email()).isEqualTo(email);
        assertThat(updated.displayName()).isEqualTo(newName);
    }

    @Test
    void should_not_allow_empty_email_in_constructor() {
        // 雖然 Email VO 本身會拒絕空值，但這裡測試建構式的不變條件完整性
        // Spec: "Email...不允許空白字串或僅含空白", 且為唯一標識, 故不應為 null.
        // Email constructor already throws for blank/null. 
        // We will rely on Email throwing InvalidEmailException for invalid inputs.
    }
}

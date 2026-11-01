package com.github.chunpingwang.ollamaagentoutcome.domain;


import static org.assertj.core.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class ProfileTest {

    private final String validId = UUID.randomUUID().toString();
    private final Email email = new Email("user@example.com");
    private final DisplayName displayName = new DisplayName("測試用戶");

    @Test
    void should_create_profile_with_valid_data() {
        Profile profile = Profile.create(new ProfileId(validId), email, displayName);
        
        assertThat(profile.id()).isEqualTo(new ProfileId(validId));
        assertThat(profile.email()).isEqualTo(email);
        assertThat(profile.displayName()).isEqualTo(displayName);
    }

    @Test
    void should_update_display_name_successfully() {
        Profile profile = Profile.create(new ProfileId(validId), email, displayName);
        DisplayName newName = new DisplayName("新名稱");
        
        profile.update(null, newName);
        
        assertThat(profile.displayName()).isEqualTo(newName);
        assertThat(profile.email()).isEqualTo(email); // 電子郵件應保持不變
    }

    @Test
    void should_reject_invalid_email_when_updating() {
        Profile profile = Profile.create(new ProfileId(validId), email, displayName);
        
        assertThatThrownBy(() -> profile.update(new Email("invalid-email"), null))
                .isInstanceOf(InvalidEmailException.class)
                .hasMessageContaining("format is invalid");
    }

    @Test
    void should_allow_same_email_when_updating() {
        Profile profile = Profile.create(new ProfileId(validId), email, displayName);
        
        // 更新為相同的電子郵件不應拋出重複例外，因為這是同一個實體的更新
        profile.update(email, new DisplayName("Updated"));
        
        assertThat(profile.email()).isEqualTo(email);
    }
}

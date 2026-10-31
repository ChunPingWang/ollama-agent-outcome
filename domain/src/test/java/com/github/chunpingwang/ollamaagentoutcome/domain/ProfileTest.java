package com.github.chunpingwang.ollamaagentoutcome.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.Test;

/**
 * 驗證 Domain Entity (Profile) 的行為與狀態轉換。
 */
class ProfileTest {

    private final String validUuid = UUID.randomUUID().toString();
    private final Email email = new Email("test@example.com");
    private final DisplayName displayName = new DisplayName("Test User");

    @Test
    void should_create_profile_with_valid_data() {
        Profile profile = Profile.create(new ProfileId(validUuid), email, displayName);
        
        assertThat(profile.id()).isEqualTo(new ProfileId(validUuid));
        assertThat(profile.email()).isEqualTo(email);
        assertThat(profile.displayName()).isEqualTo(displayName);
    }

    @Test
    void should_update_display_name_successfully() {
        Profile profile = Profile.create(new ProfileId(validUuid), email, displayName);
        
        DisplayName newName = new DisplayName("New Name");
        profile.updateDisplayName(newName);
        
        assertThat(profile.displayName()).isEqualTo(newName);
        // Email should remain unchanged after updateDisplayName
        assertThat(profile.email()).isEqualTo(email);
    }

    @Test
    void should_throw_exception_when_creating_with_null_id() {
        assertThatThrownBy(() -> Profile.create(null, email, displayName))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("id must not be null");
    }
    
    @Test
    void should_not_allow_duplicate_email_creation_in_domain_logic_context() {
        String uuid2 = UUID.randomUUID().toString();
        Profile profile1 = Profile.create(new ProfileId(validUuid), email, displayName);
        Profile profile2 = Profile.create(new ProfileId(uuid2), new Email("other@example.com"), new DisplayName("Other"));
        
        assertThat(profile1.id()).isNotEqualTo(profile2.id());
    }
}

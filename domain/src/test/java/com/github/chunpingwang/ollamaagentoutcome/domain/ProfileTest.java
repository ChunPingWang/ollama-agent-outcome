package com.github.chunpingwang.ollamaagentoutcome.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class ProfileTest {

    @Test
    void should_create_profile_with_valid_data() {
        var id = new ProfileId(UUID.randomUUID().toString());
        var email = new Email("test@example.com");
        var displayName = new DisplayName("John Doe");

        var profile = new Profile(id, email, displayName);

        assertThat(profile.id()).isEqualTo(id);
        assertThat(profile.email()).isEqualTo(email);
        assertThat(profile.displayName()).isEqualTo(displayName);
    }

    @Test
    void should_update_display_name_successfully() {
        var id = new ProfileId(UUID.randomUUID().toString());
        var email = new Email("test@example.com");
        var oldDisplayName = new DisplayName("Old Name");
        
        var profile = new Profile(id, email, oldDisplayName);

        var newDisplayName = new DisplayName("New Name");
        profile.updateDisplayName(newDisplayName);

        assertThat(profile.displayName()).isEqualTo(newDisplayName);
        // 其他欄位應保持不變
        assertThat(profile.email()).isEqualTo(email);
        assertThat(profile.id()).isEqualTo(id);
    }

    @Test
    void should_throw_when_updating_to_invalid_display_name() {
        var id = new ProfileId(UUID.randomUUID().toString());
        var email = new Email("test@example.com");
        var validDisplayName = new DisplayName("Valid Name");
        
        var profile = new Profile(id, email, validDisplayName);

        assertThatThrownBy(() -> profile.updateDisplayName(null))
                .isInstanceOf(InvalidDisplayNameException.class)
                .hasMessageContaining("顯示名稱不能為空");
                
        assertThatThrownBy(() -> profile.updateDisplayName(new DisplayName("   ")))
                .isInstanceOf(InvalidDisplayNameException.class)
                .hasMessageContaining("顯示名稱不能為空");
    }
}

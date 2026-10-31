package com.github.chunpingwang.ollamaagentoutcome.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class ProfileTest {

    @Test
    void should_create_profile_with_valid_data() {
        var id = new UUID(1L, 2L);
        var email = new com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Email("user@example.com");
        var displayName = new com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.DisplayName("John Doe");
        var bio = new com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Bio("Hello World");

        // 修正：將 raw UUID 包裝為 ProfileId 後傳入建構子
        var profile = new Profile(new com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.ProfileId(id), email, displayName, bio);

        assertThat(profile.getId().value()).isEqualTo(id);
        assertThat(profile.getEmail().value()).isEqualTo("user@example.com");
        assertThat(profile.getDisplayName().value()).isEqualTo("John Doe");
        assertThat(profile.getBio().value()).isEqualTo("Hello World");
    }

    @Test
    void should_update_display_name() {
        var id = new UUID(1L, 2L);
        var email = new com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Email("user@example.com");
        var oldName = new com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.DisplayName("Old Name");
        var bio = new com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Bio(null);

        var profile = new Profile(new com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.ProfileId(id), email, oldName, bio);

        var newName = new com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.DisplayName("New Name");
        profile.setDisplayName(newName);

        assertThat(profile.getDisplayName().value()).isEqualTo("New Name");
    }

    @Test
    void should_reject_invalid_email_on_creation() {
        var id = new UUID(1L, 2L);
        var displayName = new com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.DisplayName("Valid Name");
        var bio = new com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Bio(null);

        // 修正：包裝 ID，並驗證 null email 觸發例外
        assertThatThrownBy(() -> new Profile(new com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.ProfileId(id), null, displayName, bio))
                .isInstanceOf(com.github.chunpingwang.ollamaagentoutcome.domain.exception.InvalidEmailException.class)
                .hasMessageContaining("Invalid email address: cannot be empty or blank");
    }

    @Test
    void should_reject_empty_display_name_on_creation() {
        var id = new UUID(1L, 2L);
        var email = new com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Email("user@example.com");
        var bio = new com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Bio(null);

        // DisplayName 建構子會拋出 InvalidDisplayNameException，此測試間接驗證了該行為
        assertThatThrownBy(() -> new Profile(new com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.ProfileId(id), email, new com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.DisplayName(""), bio))
                .isInstanceOf(com.github.chunpingwang.ollamaagentoutcome.domain.exception.InvalidDisplayNameException.class);
    }

    @Test
    void should_allow_null_bio() {
        var id = new UUID(1L, 2L);
        var email = new com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Email("user@example.com");
        var displayName = new com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.DisplayName("Name");

        var profile = new Profile(new com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.ProfileId(id), email, displayName, null);

        assertThat(profile.getBio()).isNull();
    }
}

package com.github.chunpingwang.ollamaagentoutcome.domain;


import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class ProfileEntityTest {

    private final UUID id = UUID.randomUUID();
    private final Email email = new Email("test@example.com");
    private final DisplayName initialDisplayName = new DisplayName("Initial Name");

    @Test
    void should_create_profile_with_initial_values() {
        Profile profile = new Profile(id, email, initialDisplayName);

        assertThat(profile.id()).isEqualTo(id);
        assertThat(profile.email()).isEqualTo(email);
        assertThat(profile.displayName()).isEqualTo(initialDisplayName);
    }

    @Test
    void should_change_display_name_to_new_value() {
        Profile profile = new Profile(id, email, initialDisplayName);
        DisplayName newName = new DisplayName("New Name");

        profile.changeDisplayName(newName);

        assertThat(profile.displayName().value()).isEqualTo("New Name");
        // 確保其他欄位未改變
        assertThat(profile.email()).isEqualTo(email);
        assertThat(profile.id()).isEqualTo(id);
    }

    @Test
    void should_allow_changing_display_name_to_null() {
        Profile profile = new Profile(id, email, initialDisplayName);

        profile.changeDisplayName(null);

        assertThat(profile.displayName()).isNull();
    }

    @Test
    void should_not_alter_email_after_creation() {
        Profile profile = new Profile(id, email, initialDisplayName);
        
        // Email 是 Value Object，不可變；Profile 也不提供 changeEmail 方法
        // 此測試驗證結構設計：無法直接修改 email
        assertThat(profile.email().value()).isEqualTo("test@example.com");
    }
}

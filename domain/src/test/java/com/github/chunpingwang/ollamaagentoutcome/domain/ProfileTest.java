package com.github.chunpingwang.ollamaagentoutcome.domain;


import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class ProfileTest {

    private final String validUuid = "550e8400-e29b-41d4-a716-446655440000";
    
    @Test
    void should_create_profile_with_valid_data() {
        Email email = new Email("test@example.com");
        DisplayName displayName = new DisplayName("Tester");
        
        Profile profile = Profile.create(new ProfileId(validUuid), email, displayName);
        
        assertThat(profile.id().value()).isEqualTo(validUuid);
        assertThat(profile.email().address()).isEqualTo("test@example.com");
        assertThat(profile.displayName().name()).isEqualTo("Tester");
    }

    @Test
    void should_update_display_name_and_email() {
        Email initialEmail = new Email("old@test.com");
        Profile profile = Profile.create(
            new ProfileId(validUuid), 
            initialEmail, 
            new DisplayName("Old Name")
        );
        
        Email newEmail = new Email("new@test.com");
        DisplayName newName = new DisplayName("New Name");
        
        profile.update(newEmail, newName);
        
        assertThat(profile.email().address()).isEqualTo("new@test.com");
        assertThat(profile.displayName().name()).isEqualTo("New Name");
    }

    @Test
    void should_throw_when_creating_with_invalid_id_format() {
        // 假設 create 方法內部會驗證 ID，或者建構子會驗證。
        // 根據 T1 描述，ProfileId 本身會驗證 UUID。這裡測試如果傳入非法格式給 ProfileId 建構子
        assertThatThrownBy(() -> Profile.create(new ProfileId("bad-id"), new Email("a@b.c"), new DisplayName("N")))
            .isInstanceOf(IllegalArgumentException.class);
    }
}

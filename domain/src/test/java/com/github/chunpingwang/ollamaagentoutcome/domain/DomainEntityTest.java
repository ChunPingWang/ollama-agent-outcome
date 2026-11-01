package com.github.chunpingwang.ollamaagentoutcome.domain;


import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class DomainEntityTest {

    private final ProfileId id = new ProfileId(UUID.randomUUID());
    
    @Test
    void should_create_profile_with_valid_data() {
        Email email = new Email("test@example.com");
        DisplayName name = new DisplayName("Tester");
        
        Profile profile = new Profile(id, email, name);
        
        assertThat(profile.getId()).isEqualTo(id);
        assertThat(profile.getEmail().value()).isEqualTo("test@example.com");
        assertThat(profile.getDisplayName().value()).isEqualTo("Tester");
    }

    @Test
    void should_reject_null_email_on_creation() {
        assertThatThrownBy(() -> new Profile(id, null, new DisplayName("Test")))
                .isInstanceOf(NullPointerException.class) // 或 InvalidEmailException，取決於建構子實作
                .hasMessageContaining("email must not be null");
    }

    @Test
    void should_update_display_name() {
        Email email = new Email("update@test.com");
        Profile profile = new Profile(id, email, new DisplayName("Old Name"));
        
        profile.updateDisplayName(new DisplayName("New Name"));
        
        assertThat(profile.getDisplayName().value()).isEqualTo("New Name");
    }
    
    @Test
    void should_not_change_display_name_if_new_is_null_in_constructor_logic_but_method_call() {
         // 測試 updateDisplayName 行為：若傳入 null，根據上述實作不會改變（因為 if (newDisplayName != null)）
         // 但通常 DTO 更新時會明確傳遞空值。這裡驗證基本邏輯。
         Email email = new Email("nochange@test.com");
         Profile profile = new Profile(id, email, new DisplayName("Original"));
         
         profile.updateDisplayName(null); 
         
         assertThat(profile.getDisplayName().value()).isEqualTo("Original");
    }
}

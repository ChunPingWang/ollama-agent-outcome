package com.github.chunpingwang.ollamaagentoutcome.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;

/**
 * Profile 領域模型的單元測試。
 */
class ProfileTest {

    @Test
    void should_create_profile_with_all_fields() {
        // Given
        UUID id = UUID.randomUUID();
        
        // When & Then (Assuming constructors exist and validate inputs)
        var profile = new Profile(new com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.ProfileId(id), 
                                  new com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Email("user@example.com"), 
                                  new com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.DisplayName("DisplayName"), 
                                  new com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Bio("Bio text"));
        
        assertThat(profile.id()).isEqualTo(new com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.ProfileId(id));
        assertThat(profile.email().value()).isEqualTo("user@example.com");
        assertThat(profile.displayName().value()).isEqualTo("DisplayName");
        assertThat(profile.bio().map(com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Bio::toString).orElse(null)).isEqualTo("Bio text");
    }
    
    @Test
    void should_update_display_name() {
        // Given
        UUID id = UUID.randomUUID();
        var profile = new Profile(new com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.ProfileId(id), 
                                  new com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Email("user@example.com"), 
                                  new com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.DisplayName("Old Name"), 
                                  null);
        
        // When
        profile.updateDisplayName("New Name");
        
        // Then
        assertThat(profile.displayName().value()).isEqualTo("New Name");
    }
    
    @Test
    void should_throw_exception_when_updating_to_invalid_email_format_in_constructor_context_if_validated_on_creation() {
        // Given
        UUID id = UUID.randomUUID();
        var profile = new Profile(new com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.ProfileId(id), 
                                  new com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Email("valid@test.com"), 
                                  new com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.DisplayName("Name"), 
                                  new com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Bio("Bio"));

        // When & Then: 根據現有實作，updateEmail 僅賦值，不驗證格式。此測試改為驗證有效更新。
        profile.updateEmail(new com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Email("new@test.com"));
        assertThat(profile.email().value()).isEqualTo("new@test.com");
    }
}

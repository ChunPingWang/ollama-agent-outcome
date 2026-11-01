package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;



import static org.assertj.core.api.Assertions.*;import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

import org.junit.jupiter.api.Test;

/**
 * 驗證 Domain Profile 與 JPA Entity 之間的轉換邏輯。
 */
class ProfileConverterTest {

    private final UUID testUuid = UUID.randomUUID();
    
    @Test
    void should_convert_domain_to_entity() {
        // Given
        Profile profile = new Profile(
            new ProfileId(testUuid), 
            new Email("converter@test.com"), 
            new DisplayName("Converter User")
        );

        // When
        ProfileEntity entity = ProfileConverter.toEntity(profile);

        // Then
        assertThat(entity.getId()).isEqualTo(testUuid);
        assertThat(entity.getEmail()).isEqualTo("converter@test.com");
        assertThat(entity.getDisplayName()).isEqualTo("Converter User");
    }

    @Test
    void should_convert_entity_to_domain() {
        // Given
        ProfileEntity entity = new ProfileEntity();
        entity.setId(testUuid);
        entity.setEmail("entity@test.com");
        entity.setDisplayName("Entity User");

        // When
        Profile profile = ProfileConverter.toDomain(entity);

        // Then
        assertThat(profile.getId().getValue()).isEqualTo(testUuid);
        assertThat(profile.getEmail().getAddress()).isEqualTo("entity@test.com");
        assertThat(profile.getDisplayName().getName()).isEqualTo("Entity User");
    }
    
    @Test
    void should_handle_null_display_name_in_conversion() {
        // Given Domain with null display name
        Profile domainWithNullName = new Profile(
            new ProfileId(UUID.randomUUID()), 
            new Email("nullname@test.com"), 
            new DisplayName(null)
        );

        // When
        ProfileEntity entity = ProfileConverter.toEntity(domainWithNullName);

        // Then
        assertThat(entity.getDisplayName()).isNull();
        
        // Convert back
        Profile convertedBack = ProfileConverter.toDomain(entity);
        assertThat(convertedBack.getDisplayName().getName()).isNull();
    }
}

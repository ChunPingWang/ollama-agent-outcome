package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.github.chunpingwang.ollamaagentoutcome.domain.Bio;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 純 Java 測試，驗證 Domain 與 Entity 的雙向轉換邏輯。
 */
class ProfileConverterTest {

    @Test
    @DisplayName("應能正確轉換 Domain 到 Entity")
    void should_convert_domain_to_entity_correctly() {
        // Given
        String id = UUID.randomUUID().toString();
        Profile profile = new Profile(
            new ProfileId(id),
            new Email("converter@test.com"),
            new Name("Converter"),
            new Bio("Test Bio")
        );

        // When
        ProfileEntity entity = ProfileConverter.toEntity(profile);

        // Then
        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getEmail()).isEqualTo("converter@test.com");
        assertThat(entity.getName()).isEqualTo("Converter");
        assertThat(entity.getBio()).isEqualTo("Test Bio");
        assertThat(entity.isNew()).isTrue();
    }

    @Test
    @DisplayName("應能正確轉換 Entity 到 Domain")
    void should_convert_entity_to_domain_correctly() {
        // Given
        ProfileEntity entity = new ProfileEntity();
        entity.setId("conv-id");
        entity.setEmail("conv@test.com");
        entity.setName("Conv Name");
        entity.setBio("Conv Bio");
        entity.setNew(false);

        // When
        Profile profile = ProfileConverter.toDomain(entity);

        // Then
        assertThat(profile.id().value()).isEqualTo("conv-id");
        assertThat(profile.email().value()).isEqualTo("conv@test.com");
        assertThat(profile.name().value()).isEqualTo("Conv Name");
        assertThat(profile.bio().value()).isEqualTo("Conv Bio");
    }

    @Test
    @DisplayName("Bio 為 null 時應轉換為空字串")
    void should_handle_null_bio() {
        // Given
        Profile profile = new Profile(
            new ProfileId("id"),
            new Email("bio@test.com"),
            new Name("Name"),
            new Bio(null)
        );

        // When
        ProfileEntity entity = ProfileConverter.toEntity(profile);

        // Then
        assertThat(entity.getBio()).isEqualTo("");
    }
}

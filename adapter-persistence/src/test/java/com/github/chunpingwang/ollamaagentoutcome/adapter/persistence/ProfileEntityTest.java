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
 * 測試 JPA Entity 的映射與 Persistable 行為。
 */
class ProfileEntityTest {

    @Test
    @DisplayName("應能將 Domain Profile 轉換為 JPA Entity")
    void should_convert_domain_to_entity() {
        // Given
        String id = UUID.randomUUID().toString();
        Profile domainProfile = new Profile(
            new ProfileId(id),
            new Email("test@example.com"),
            new Name("Test User"),
            new Bio("Bio content")
        );

        // When
        ProfileEntity entity = ProfileConverter.toEntity(domainProfile);

        // Then
        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getEmail()).isEqualTo("test@example.com");
        assertThat(entity.getName()).isEqualTo("Test User");
        assertThat(entity.getBio()).isEqualTo("Bio content");
        assertThat(entity.isNew()).isTrue();
    }

    @Test
    @DisplayName("應能將 JPA Entity 轉換為 Domain Profile")
    void should_convert_entity_to_domain() {
        // Given
        ProfileEntity entity = new ProfileEntity();
        entity.setId("uuid-123");
        entity.setEmail("user@example.com");
        entity.setName("User Name");
        entity.setBio("My Bio");
        entity.setNew(false); // Simulate loaded from DB

        // When
        Profile profile = ProfileConverter.toDomain(entity);

        // Then
        assertThat(profile.id().value()).isEqualTo("uuid-123");
        assertThat(profile.email().value()).isEqualTo("user@example.com");
        assertThat(profile.name().value()).isEqualTo("User Name");
        assertThat(profile.bio().value()).isEqualTo("My Bio");
    }

    @Test
    @DisplayName("新建立的 Entity 應標示為 isNew")
    void should_be_new_when_created() {
        // Given
        ProfileEntity entity = new ProfileEntity();
        entity.setId("new-id");
        entity.setEmail("new@example.com");
        entity.setName("New");
        entity.setBio(null);

        // When & Then
        assertThat(entity.isNew()).isTrue();
    }

    @Test
    @DisplayName("從資料庫載入的 Entity 應標示為 not new")
    void should_not_be_new_when_loaded() {
        // Given
        ProfileEntity entity = new ProfileEntity();
        entity.setId("existing-id");
        entity.setEmail("existing@example.com");
        entity.setName("Existing");
        entity.setBio("Bio");
        entity.setNew(false);

        // When & Then
        assertThat(entity.isNew()).isFalse();
    }
}

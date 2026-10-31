package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.github.chunpingwang.ollamaagentoutcome.domain.Age;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 測試 Domain Profile 與 JPA ProfileEntity 之間的雙向轉換。
 */
class ProfileEntityConverterTest {

    @Test
    void should_convert_profile_to_entity() {
        // Given
        UUID id = UUID.randomUUID();
        ProfileId profileId = new ProfileId(id);
        Email email = new Email("user@example.com");
        Name name = new Name("John Doe");
        Age age = new Age(25);
        Profile profile = new Profile(profileId, email, name, age);

        // When
        ProfileEntity entity = ProfileEntityConverter.toEntity(profile);

        // Then
        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getEmail()).isEqualTo("user@example.com");
        assertThat(entity.getName()).isEqualTo("John Doe");
        assertThat(entity.getAge()).isEqualTo(25);
        assertThat(entity.isNew()).isTrue(); // New entity should be marked as new
    }

    @Test
    void should_convert_entity_to_profile() {
        // Given
        UUID id = UUID.randomUUID();
        ProfileEntity entity = new ProfileEntity();
        entity.setId(id);
        entity.setEmail("user@example.com");
        entity.setName("John Doe");
        entity.setAge(25);
        entity.setNew(false);

        // When
        Profile profile = ProfileEntityConverter.toDomain(entity);

        // Then
        assertThat(profile.getId().getValue()).isEqualTo(id);
        assertThat(profile.getEmail().getValue()).isEqualTo("user@example.com");
        assertThat(profile.getName().getValue()).isEqualTo("John Doe");
        assertThat(profile.getAge().getValue()).isEqualTo(25);
    }
}

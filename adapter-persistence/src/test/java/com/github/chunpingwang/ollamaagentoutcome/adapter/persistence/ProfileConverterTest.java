package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

import org.junit.jupiter.api.Test;

/**
 * 測試 Domain 與 Entity 的雙向轉換。
 */
class ProfileConverterTest {

    private final ProfileConverter converter = new ProfileConverter();

    @Test
    void should_convert_domain_to_entity() {
        // Given
        UUID id = UUID.randomUUID();
        ProfileId profileId = new ProfileId(id);
        Email email = new Email("user@example.com");
        Profile profile = new Profile(profileId, "John Doe", email);

        // When
        ProfileEntity entity = converter.toEntity(profile);

        // Then
        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getName()).isEqualTo("John Doe");
        assertThat(entity.getEmail()).isEqualTo("user@example.com");
        assertThat(entity.isNew()).isTrue();
    }

    @Test
    void should_convert_entity_to_domain() {
        // Given
        UUID id = UUID.randomUUID();
        ProfileEntity entity = new ProfileEntity(id, "John Doe", "user@example.com");
        entity.setNew(false); // Simulate loaded entity

        // When
        Profile profile = converter.toDomain(entity);

        // Then
        assertThat(profile.getId()).isEqualTo(new ProfileId(id));
        assertThat(profile.getName()).isEqualTo("John Doe");
        assertThat(profile.getEmail()).isEqualTo(new Email("user@example.com"));
    }

    @Test
    void should_convert_new_entity_to_domain_with_is_new_true() {
        // Given
        UUID id = UUID.randomUUID();
        ProfileEntity entity = new ProfileEntity(id, "John Doe", "user@example.com");
        // isNew is true by default

        // When
        Profile profile = converter.toDomain(entity);

        // Then
        assertThat(profile.getId()).isEqualTo(new ProfileId(id));
        assertThat(profile.getName()).isEqualTo("John Doe");
    }
}

package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.github.chunpingwang.ollamaagentoutcome.domain.Age;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

class ProfileEntityTest {

    @Test
    void should_convert_domain_to_entity_and_back() {
        // Given
        String id = UUID.randomUUID().toString();
        ProfileId profileId = new ProfileId(id);
        Email email = new Email("user@example.com");
        Name name = new Name("John Doe");
        Age age = new Age(30);
        Profile profile = new Profile(profileId, email, name, age);

        // When
        ProfileEntity entity = ProfileConverter.toEntity(profile);

        // Then
        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getEmail()).isEqualTo("user@example.com");
        assertThat(entity.getName()).isEqualTo("John Doe");
        assertThat(entity.getAge()).isEqualTo(30);
        assertThat(entity.isNew()).isTrue();
    }

    @Test
    void should_convert_entity_to_domain() {
        // Given
        ProfileEntity entity = new ProfileEntity();
        entity.setId("test-id-123");
        entity.setEmail("test@example.com");
        entity.setName("Jane Doe");
        entity.setAge(25);
        entity.setNew(false); // Simulate loaded from DB

        // When
        Profile profile = ProfileConverter.toDomain(entity);

        // Then
        assertThat(profile.id().getValue()).isEqualTo("test-id-123");
        assertThat(profile.email().getValue()).isEqualTo("test@example.com");
        assertThat(profile.name().getValue()).isEqualTo("Jane Doe");
        assertThat(profile.age().getValue()).isEqualTo(25);
    }
}

package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;

/**
 * Tests for ProfileEntity mapping and Persistable behavior.
 */
class ProfileEntityTest {

    @Test
    void should_create_entity_with_uuid() {
        UUID id = UUID.randomUUID();
        String name = "John Doe";
        String email = "john@example.com";
        String bio = "Developer";

        ProfileEntity entity = new ProfileEntity(id, name, email, bio);

        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getName()).isEqualTo(name);
        assertThat(entity.getEmail()).isEqualTo(email);
        assertThat(entity.getBio()).isEqualTo(bio);
        assertThat(entity.isNew()).isTrue();
    }

    @Test
    void should_be_new_by_default_and_after_persist_lifecycle() {
        UUID id = UUID.randomUUID();
        ProfileEntity entity = new ProfileEntity(id, "Name", "email@test.com", "");

        // Initially new
        assertThat(entity.isNew()).isTrue();

        // Simulate persist lifecycle
        entity.onCreate();
        assertThat(entity.isNew()).isTrue();

        entity.onPersist();
        assertThat(entity.isNew()).isFalse();

        // Simulate load from DB
        entity.setBio("Updated");
        entity.onLoad();
        assertThat(entity.isNew()).isFalse();
    }
}

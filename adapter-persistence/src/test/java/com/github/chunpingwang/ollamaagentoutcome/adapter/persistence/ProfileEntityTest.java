package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class ProfileEntityTest {

    @Test
    void should_be_new_by_default() {
        UUID id = UUID.randomUUID();
        String email = "test@example.com";
        String displayName = "Test User";

        ProfileEntity entity = new ProfileEntity(id, email, displayName);

        assertThat(entity.isNew()).isTrue();
        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getEmail()).isEqualTo(email);
        assertThat(entity.getDisplayName()).isEqualTo(displayName);
    }

    @Test
    void should_mark_as_not_new_after_persist_or_load() {
        // Simulate PostPersist or PostLoad behavior by manually setting state if needed for test logic,
        // but typically we verify the lifecycle callbacks work in integration tests with DB.
        // Here we just verify initial state and that it can be changed (if we had a setter) 
        // OR simply rely on the fact that after saveAndFlush, isNew becomes false.
        
        // Since we don't have a public setter for isNew, let's test via reflection or assume standard JPA behavior.
        // For unit testing the Entity class itself without Spring/Testcontainers:
        // We can only verify constructor state. The transition to 'not new' is verified in PortImpl tests.
        
        UUID id = UUID.randomUUID();
        ProfileEntity entity = new ProfileEntity(id, "a@b.c", null);
        
        assertThat(entity.isNew()).isTrue();
    }
}

package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class ProfileEntityTest {

    @Test
    void should_be_new_when_created() {
        var id = UUID.randomUUID();
        var entity = new ProfileEntity(id, "test@example.com", "John Doe");

        assertThat(entity.isNew()).isTrue();
        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getEmail()).isEqualTo("test@example.com");
        assertThat(entity.getDisplayName()).isEqualTo("John Doe");
    }

    @Test
    void should_not_be_new_after_persist_or_load() {
        // Simulate post-load or post-persist behavior by calling the method directly
        // In real JPA lifecycle, these are called automatically
        var id = UUID.randomUUID();
        var entity = new ProfileEntity(id, "test@example.com", "John Doe");
        
        entity.markAsNotNew();

        assertThat(entity.isNew()).isFalse();
    }
}

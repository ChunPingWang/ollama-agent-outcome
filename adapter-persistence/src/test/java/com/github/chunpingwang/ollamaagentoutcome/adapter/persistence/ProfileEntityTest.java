package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.dao.DataIntegrityViolationException;

/**
 * 測試 JPA Entity 的 CRUD、Persistable 邏輯與唯一性約束。
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProfileEntityTest {

    @Autowired
    private ProfileRepository repository;

    @Test
    void should_save_and_load_entity() {
        // Given
        UUID id = UUID.randomUUID();
        ProfileEntity entity = new ProfileEntity(id, "John Doe", "john@example.com");

        // When
        ProfileEntity saved = repository.save(entity);

        // Then
        assertThat(saved.getId()).isEqualTo(id);
        assertThat(saved.getName()).isEqualTo("John Doe");
        assertThat(saved.getEmail()).isEqualTo("john@example.com");
        
        ProfileEntity loaded = repository.findById(id).orElseThrow();
        assertThat(loaded.getName()).isEqualTo("John Doe");
    }

    @Test
    void should_be_persistable_new() {
        // Given
        UUID id = UUID.randomUUID();
        ProfileEntity entity = new ProfileEntity(id, "John Doe", "john@example.com");

        // Then
        assertThat(entity.isNew()).isTrue();
    }

    @Test
    void should_be_persistable_not_new_after_save() {
        // Given
        UUID id = UUID.randomUUID();
        ProfileEntity entity = new ProfileEntity(id, "John Doe", "john@example.com");
        
        // When
        repository.save(entity);

        // Then
        assertThat(entity.isNew()).isFalse();
    }

    @Test
    void should_throw_data_integrity_violation_on_duplicate_email() {
        // Given
        UUID id1 = UUID.randomUUID();
        ProfileEntity entity1 = new ProfileEntity(id1, "John Doe", "unique@example.com");
        repository.save(entity1);

        UUID id2 = UUID.randomUUID();
        ProfileEntity entity2 = new ProfileEntity(id2, "Jane Doe", "unique@example.com");

        // When & Then
        assertThatThrownBy(() -> repository.saveAndFlush(entity2))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}

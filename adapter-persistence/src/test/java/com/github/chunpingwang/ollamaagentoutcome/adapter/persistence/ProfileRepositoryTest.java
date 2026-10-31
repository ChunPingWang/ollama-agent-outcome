package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

/**
 * 測試 ProfileRepository 的 CRUD 操作與唯一性約束。
 * 使用 @DataJpaTest + Testcontainers PostgreSQL。
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
@DirtiesContext
class ProfileRepositoryTest {

    @Autowired
    private ProfileRepository profileRepository;

    @Test
    void should_save_and_find_by_id() {
        // Given
        UUID id = UUID.randomUUID();
        ProfileEntity entity = new ProfileEntity();
        entity.setId(id);
        entity.setEmail("user@example.com");
        entity.setName("John Doe");
        entity.setAge(25);
        entity.setNew(false); // Simulate persisted

        // When
        ProfileEntity saved = profileRepository.save(entity);
        profileRepository.flush();

        // Then
        Optional<ProfileEntity> found = profileRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("user@example.com");
    }

    @Test
    void should_throw_exception_when_duplicate_email() {
        // Given
        UUID id1 = UUID.randomUUID();
        ProfileEntity entity1 = new ProfileEntity();
        entity1.setId(id1);
        entity1.setEmail("duplicate@example.com");
        entity1.setName("User 1");
        entity1.setAge(25);
        entity1.setNew(false);

        profileRepository.save(entity1);
        profileRepository.flush();

        UUID id2 = UUID.randomUUID();
        ProfileEntity entity2 = new ProfileEntity();
        entity2.setId(id2);
        entity2.setEmail("duplicate@example.com"); // Duplicate
        entity2.setName("User 2");
        entity2.setAge(30);
        entity2.setNew(false);

        // When & Then
        assertThatThrownBy(() -> profileRepository.saveAndFlush(entity2))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void should_delete_by_id() {
        // Given
        UUID id = UUID.randomUUID();
        ProfileEntity entity = new ProfileEntity();
        entity.setId(id);
        entity.setEmail("delete@example.com");
        entity.setName("Delete Me");
        entity.setAge(20);
        entity.setNew(false);
        profileRepository.save(entity);
        profileRepository.flush();

        // When
        profileRepository.deleteById(id);
        profileRepository.flush();

        // Then
        assertThat(profileRepository.findById(id)).isEmpty();
    }
}

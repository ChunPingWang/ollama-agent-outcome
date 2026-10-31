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
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class ProfileJpaRepositoryTest {

    @Autowired
    private ProfileJpaRepository repository;

    @Test
    void should_save_and_find_profile() {
        // Given
        String id = UUID.randomUUID().toString();
        ProfileEntity entity = new ProfileEntity();
        entity.setId(id);
        entity.setEmail("unique@example.com");
        entity.setName("Test User");
        entity.setAge(30);
        entity.setNew(true);

        // When
        ProfileEntity saved = repository.save(entity);

        // Then
        Optional<ProfileEntity> found = repository.findById(id);
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("unique@example.com");
    }

    @Test
    void should_throw_data_integrity_violation_on_duplicate_email() {
        // Given
        String id1 = UUID.randomUUID().toString();
        ProfileEntity entity1 = new ProfileEntity();
        entity1.setId(id1);
        entity1.setEmail("duplicate@example.com");
        entity1.setName("User 1");
        entity1.setAge(30);
        entity1.setNew(true);
        repository.save(entity1);

        String id2 = UUID.randomUUID().toString();
        ProfileEntity entity2 = new ProfileEntity();
        entity2.setId(id2);
        entity2.setEmail("duplicate@example.com"); // Duplicate email
        entity2.setName("User 2");
        entity2.setAge(31);
        entity2.setNew(true);

        // When & Then
        assertThatThrownBy(() -> repository.saveAndFlush(entity2))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}

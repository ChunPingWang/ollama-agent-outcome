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

/**
 * 測試 ProfileRepository 的儲存、查詢與唯一性約束。
 * 使用 @DataJpaTest 與 Testcontainers PostgreSQL。
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProfileRepositoryTest {

    @Autowired
    private ProfileRepository profileRepository;

    @Test
    void should_save_and_find_profile_by_id() {
        // Given
        UUID id = UUID.randomUUID();
        ProfileEntity entity = new ProfileEntity();
        entity.setId(id);
        entity.setName("張三");
        entity.setEmail("zhangsan@example.com");
        entity.setNew(false); // 模擬已儲存狀態

        // When
        ProfileEntity saved = profileRepository.save(entity);
        Optional<ProfileEntity> found = profileRepository.findById(saved.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("張三");
        assertThat(found.get().getEmail()).isEqualTo("zhangsan@example.com");
    }

    @Test
    void should_find_by_email() {
        // Given
        UUID id = UUID.randomUUID();
        ProfileEntity entity = new ProfileEntity();
        entity.setId(id);
        entity.setName("李四");
        entity.setEmail("lisi@example.com");
        profileRepository.save(entity);

        // When
        Optional<ProfileEntity> found = profileRepository.findByEmail("lisi@example.com");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("李四");
    }

    @Test
    void should_throw_data_integrity_violation_on_duplicate_email() {
        // Given
        UUID id1 = UUID.randomUUID();
        ProfileEntity entity1 = new ProfileEntity();
        entity1.setId(id1);
        entity1.setName("王五");
        entity1.setEmail("unique@example.com");
        profileRepository.save(entity1);

        UUID id2 = UUID.randomUUID();
        ProfileEntity entity2 = new ProfileEntity();
        entity2.setId(id2);
        entity2.setName("趙六");
        entity2.setEmail("unique@example.com"); // 重複 Email

        // When & Then
        assertThatThrownBy(() -> profileRepository.saveAndFlush(entity2))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}

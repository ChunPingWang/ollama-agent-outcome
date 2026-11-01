package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;


import org.springframework.test.annotation.DirtiesContext;
import static org.assertj.core.api.Assertions.*;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProfileEntityTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProfileRepository profileRepository;

    @Test
    void should_save_and_load_profile() {
        // given
        String id = UUID.randomUUID().toString();
        var entity = new ProfileEntity(id, "test@example.com", "Test User");

        // when
        entityManager.persistAndFlush(entity);
        entityManager.clear();

        // then
        var found = entityManager.find(ProfileEntity.class, id);
        assertThat(found).isNotNull();
        assertThat(found.getEmail()).isEqualTo("test@example.com");
        assertThat(found.getDisplayName()).isEqualTo("Test User");
    }

    @Test
    void should_reject_duplicate_email() {
        // given
        String id1 = UUID.randomUUID().toString();
        var e1 = new ProfileEntity(id1, "unique@test.com", "User 1");
        
        // Persist and flush the first one to commit it to DB so constraint triggers on second insert
        entityManager.persist(e1);
        entityManager.flush();
        entityManager.clear();

        String id2 = UUID.randomUUID().toString();
        var e2 = new ProfileEntity(id2, "unique@test.com", "User 2");

        // when & then: Use repository.saveAndFlush to trigger Spring's exception translation
        assertThatThrownBy(() -> profileRepository.saveAndFlush(e2))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("duplicate key value violates unique constraint");
    }
    
    @Test
    void should_be_new_when_created() {
        // given
        var entity = new ProfileEntity(UUID.randomUUID().toString(), "new@example.com", "New User");
        
        // then
        assertThat(entity.isNew()).isTrue();
    }
}

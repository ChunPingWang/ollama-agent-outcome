package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;



import static org.assertj.core.api.Assertions.*;import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;

/**
 * 測試 Profile Entity 的映射、唯一約束與 isNew() 狀態。
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProfileEntityTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProfileRepository profileRepository;

    @Test
    void should_save_and_load_profile_successfully() {
        String id = UUID.randomUUID().toString();
        String email = "test@example.com";
        String displayName = "Test User";

        ProfileEntity entity = new ProfileEntity(id, email, displayName);
        
        // Initially new
        assertThat(entity.isNew()).isTrue();

        entityManager.persistAndFlush(entity);
        
        // After persist, it is no longer new
        assertThat(entity.isNew()).isFalse();

        // Load from DB
        ProfileEntity loaded = entityManager.find(ProfileEntity.class, id);
        
        assertThat(loaded).isNotNull();
        assertThat(loaded.getId()).isEqualTo(id);
        assertThat(loaded.getEmail()).isEqualTo(email.toLowerCase());
        assertThat(loaded.getDisplayName()).isEqualTo(displayName);
    }

    @Test
    void should_reject_duplicate_email_on_flush() {
        String id1 = UUID.randomUUID().toString();
        String email = "unique@example.com";
        
        ProfileEntity first = new ProfileEntity(id1, email, "First");
        entityManager.persist(first);
        entityManager.flush();
        entityManager.clear(); 
        
        String id2 = UUID.randomUUID().toString();
        ProfileEntity second = new ProfileEntity(id2, email, "Second");
        
        // The assertion must be the last line in this method because PostgreSQL aborts transaction on violation
        assertThatThrownBy(() -> profileRepository.saveAndFlush(second))
            .isInstanceOf(DataIntegrityViolationException.class);
    }
    
    @Test
    void should_handle_null_display_name() {
        String id = UUID.randomUUID().toString();
        String email = "nullname@example.com";
        
        ProfileEntity entity = new ProfileEntity(id, email, null);
        entityManager.persistAndFlush(entity);
        
        ProfileEntity loaded = entityManager.find(ProfileEntity.class, id);
        assertThat(loaded.getDisplayName()).isNull();
    }

    @Test
    void should_be_new_when_created_and_not_persisted() {
        String id = UUID.randomUUID().toString();
        ProfileEntity entity = new ProfileEntity(id, "new@test.com", "New User");
        assertThat(entity.isNew()).isTrue();
    }
}

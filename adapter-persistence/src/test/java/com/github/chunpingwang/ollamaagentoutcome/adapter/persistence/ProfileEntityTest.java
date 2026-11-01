package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;


import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.annotation.DirtiesContext;

// 使用 @Transactional 讓 TestEntityManager 有交易可用，每個測試後重建 context
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProfileEntityTest {

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void should_save_and_load_profile_entity() {
        String id = UUID.randomUUID().toString();
        ProfileEntity entity = new ProfileEntity(id, "test@example.com", "Tester");
        
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();

        var found = entityManager.find(ProfileEntity.class, id);
        assertThat(found).isNotNull();
        assertThat(found.getEmail()).isEqualTo("test@example.com");
        assertThat(found.getDisplayName()).isEqualTo("Tester");
    }

    @Test
    void should_reject_duplicate_email_on_persist() {
        String id1 = UUID.randomUUID().toString();
        ProfileEntity e1 = new ProfileEntity(id1, "unique@test.com", "User 1");
        entityManager.persist(e1);
        entityManager.flush(); // First insert succeeds
        
        String id2 = UUID.randomUUID().toString();
        ProfileEntity e2 = new ProfileEntity(id2, "unique@test.com", "User 2"); // Duplicate email
        
        // Hibernate throws ConstraintViolationException directly for unique constraint violations 
        // in this context before Spring wraps it.
        assertThatThrownBy(() -> entityManager.persistAndFlush(e2))
            .isInstanceOf(ConstraintViolationException.class);
            
        // Transaction aborted after DVE, stop assertions here to avoid further errors
    }

    @Test
    void should_be_new_when_created_manually() {
        String id = UUID.randomUUID().toString();
        ProfileEntity entity = new ProfileEntity(id, "new@example.com", "New User");
        
        assertThat(entity.isNew()).isTrue();
        
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();
        
        var found = entityManager.find(ProfileEntity.class, id);
        assertThat(found).isNotNull();
        assertThat(found.isNew()).isFalse();
    }
}

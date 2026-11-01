package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;



import static org.assertj.core.api.Assertions.*;import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;

/**
 * 測試 JpaDeleteProfile Port 的刪除行為。
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JpaDeleteProfilePort.class)
class JpaDeleteProfilePortTest {

    @Autowired
    private JpaDeleteProfilePort deleteProfilePort;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void should_delete_profile_by_id_successfully() {
        // Arrange
        String id = UUID.randomUUID().toString();
        ProfileEntity entity = new ProfileEntity(id, "delete@test.com", "Delete User");
        entityManager.persistAndFlush(entity);
        
        assertThat(entityManager.find(ProfileEntity.class, id)).isNotNull();

        // Act
        deleteProfilePort.deleteById(id);
        
        entityManager.flush();
        entityManager.clear();

        // Assert: Should be gone from DB
        assertThat(entityManager.find(ProfileEntity.class, id)).isNull();
    }
}

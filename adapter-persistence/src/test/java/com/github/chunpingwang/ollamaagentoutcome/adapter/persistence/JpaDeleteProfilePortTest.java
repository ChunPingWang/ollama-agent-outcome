package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;



import org.springframework.test.annotation.DirtiesContext;
import static org.assertj.core.api.Assertions.*;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

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
    void should_delete_profile_by_id() {
        // given
        String id = UUID.randomUUID().toString();
        var entity = new ProfileEntity(id, "delete@example.com", "Deleter");
        entityManager.persistAndFlush(entity);
        
        assertThat(entityManager.find(ProfileEntity.class, id)).isNotNull();

        // when
        deleteProfilePort.deleteById(id);
        entityManager.flush();
        entityManager.clear();

        // then
        assertThat(entityManager.find(ProfileEntity.class, id)).isNull();
    }
}

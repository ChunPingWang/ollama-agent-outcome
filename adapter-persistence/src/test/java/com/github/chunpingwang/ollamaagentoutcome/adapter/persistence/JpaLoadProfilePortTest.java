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

import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JpaLoadProfilePort.class)
class JpaLoadProfilePortTest {

    @Autowired
    private JpaLoadProfilePort loadProfilePort;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void should_load_profile_by_id() {
        // given
        String id = UUID.randomUUID().toString();
        var entity = new ProfileEntity(id, "load@example.com", "Loader");
        entityManager.persistAndFlush(entity);
        
        // when
        var result = loadProfilePort.findById(id);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().id().value()).isEqualTo(id);
        assertThat(result.get().email().address()).isEqualTo("load@example.com");
        assertThat(result.get().displayName().name()).isEqualTo("Loader");
    }

    @Test
    void should_return_empty_when_not_found() {
        // when
        var result = loadProfilePort.findById(UUID.randomUUID().toString());

        // then
        assertThat(result).isEmpty();
    }
}

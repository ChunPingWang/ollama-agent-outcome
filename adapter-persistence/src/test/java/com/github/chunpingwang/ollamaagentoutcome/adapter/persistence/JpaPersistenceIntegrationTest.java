package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;


import org.springframework.test.annotation.DirtiesContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;

// 測試使用 TestEntityManager（需要交易）：保留測試交易，以 flush()/clear() 控制一級快取；每個測試後重建 context
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({ JpaSaveProfilePort.class, JpaLoadProfilePort.class, JpaDeleteProfilePort.class })
class JpaPersistenceIntegrationTest {

    @Autowired
    private SaveProfilePort saveProfilePort;

    @Autowired
    private LoadProfilePort loadProfilePort;

    @Autowired
    private DeleteProfilePort deleteProfilePort;

    @Autowired
    private org.springframework.boot.jpa.test.autoconfigure.TestEntityManager entityManager;

    @Test
    void should_save_and_load_profile() {
        // given
        String id = UUID.randomUUID().toString();
        Profile profile = createValidProfile(id);

        // when
        saveProfilePort.save(profile);
        entityManager.flush();
        entityManager.clear();

        // then
        assertThat(loadProfilePort.findById(id)).isPresent();
        assertThat(loadProfilePort.findById(id).get().getId().value).isEqualTo(id);
    }

    @Test
    void should_throw_duplicate_email_exception_on_constraint_violation() {
        // given
        String id1 = UUID.randomUUID().toString();
        String email = "unique@example.com";
        
        Profile p1 = Profile.create(new com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId(id1), email, "User 1");
        saveProfilePort.save(p1);
        entityManager.flush();
        entityManager.clear();

        // when & then: The last line of this test method must be the assertion that throws.
        // PostgreSQL aborts transaction on constraint violation, so no further operations allowed in same tx.
        assertThatThrownBy(() -> {
            String id2 = UUID.randomUUID().toString();
            Profile p2 = Profile.create(new com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId(id2), email, "User 2");
            saveProfilePort.save(p2);
        }).isInstanceOf(DuplicateEmailException.class)
          .hasMessageContaining("Duplicate email address");
    }

    @Test
    void should_delete_profile_by_id() {
        // given
        String id = UUID.randomUUID().toString();
        Profile saved = createValidProfile(id);
        saveProfilePort.save(saved);
        entityManager.flush();
        entityManager.clear();

        // when
        deleteProfilePort.deleteById(id);
        entityManager.flush();
        entityManager.clear();

        // then
        assertThat(loadProfilePort.findById(id)).isEmpty();
    }

    private Profile createValidProfile(String id) {
        return Profile.create(
            new com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId(id), 
            "test" + System.nanoTime() + "@example.com", 
            "Test User"
        );
    }
}

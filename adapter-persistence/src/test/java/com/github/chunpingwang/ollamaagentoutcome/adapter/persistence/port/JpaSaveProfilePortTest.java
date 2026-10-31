package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.port;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

import com.github.chunpingwang.ollamaagentoutcome.domain.entity.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.exception.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Bio;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.PersistenceTestApplication;
import jakarta.persistence.EntityManager;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({ JpaSaveProfilePort.class })
class JpaSaveProfilePortTest {

    @Autowired
    private JpaSaveProfilePort saveProfilePort;

    @Autowired
    private EntityManager entityManager;

    @Test
    void should_save_profile_successfully() {
        UUID id = UUID.randomUUID();
        Profile profile = new Profile(
                new ProfileId(id),
                new Email("save@example.com"),
                new DisplayName("Saver"),
                new Bio("Saving bio"));

        Profile saved = saveProfilePort.save(profile);

        assertThat(saved).isNotNull();
        assertThat(saved.id().value()).isEqualTo(id);
        
        // Flush and clear to verify persistence in DB
        entityManager.flush();
        entityManager.clear();

        var entity = entityManager.find(com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.entity.ProfileEntity.class, id);
        assertThat(entity).isNotNull();
        assertThat(entity.getEmail()).isEqualTo("save@example.com");
    }

    @Test
    void should_throw_duplicate_email_exception_when_email_exists() {
        UUID id1 = UUID.randomUUID();
        Profile profile1 = new Profile(
                new ProfileId(id1),
                new Email("dup@example.com"),
                new DisplayName("User 1"),
                null);
        saveProfilePort.save(profile1);
        entityManager.flush();
        entityManager.clear();

        UUID id2 = UUID.randomUUID();
        Profile profile2 = new Profile(
                new ProfileId(id2),
                new Email("dup@example.com"),
                new DisplayName("User 2"),
                null);

        assertThatThrownBy(() -> saveProfilePort.save(profile2))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("Duplicate email address: unknown");
    }
}

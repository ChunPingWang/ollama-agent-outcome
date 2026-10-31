package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.port;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.chunpingwang.ollamaagentoutcome.domain.exception.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.ProfileId;
import java.time.Instant;
import java.util.UUID;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({ JpaLoadProfilePort.class, JpaSaveProfilePort.class })
class PersistencePortTest {

    @Autowired
    private JpaLoadProfilePort loadProfilePort;

    @Autowired
    private JpaSaveProfilePort saveProfilePort;

    @Autowired
    private EntityManager entityManager;

    @Test
    void should_save_and_load_profile() {
        // Given
        UUID id = UUID.randomUUID();
        Profile profile = new Profile(
                new ProfileId(id),
                new Name("王小明"),
                new Email("xiaoming@example.com"),
                Instant.now()
        );

        // When
        saveProfilePort.save(profile);
        Profile loaded = loadProfilePort.findById(new ProfileId(id)).orElseThrow();

        // Then
        assertThat(loaded.getId().value()).isEqualTo(id);
        assertThat(loaded.getName().text()).isEqualTo("王小明");
        assertThat(loaded.getEmail().address()).isEqualTo("xiaoming@example.com");
    }

    @Test
    void should_throw_duplicate_email_exception_on_duplicate_email() {
        // Given
        UUID id1 = UUID.randomUUID();
        Profile profile1 = new Profile(
                new ProfileId(id1),
                new Name("User 1"),
                new Email("unique@test.com"),
                Instant.now()
        );
        saveProfilePort.save(profile1);

        UUID id2 = UUID.randomUUID();
        Profile profile2 = new Profile(
                new ProfileId(id2),
                new Name("User 2"),
                new Email("unique@test.com"), // Duplicate email
                Instant.now()
        );

        // When & Then
        assertThatThrownBy(() -> saveProfilePort.save(profile2))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("email already exists");
    }

    @Test
    void should_delete_profile_by_id() {
        // Given
        UUID id = UUID.randomUUID();
        Profile profile = new Profile(
                new ProfileId(id),
                new Name("To Delete"),
                new Email("delete@test.com"),
                Instant.now()
        );
        saveProfilePort.save(profile);

        // When
        saveProfilePort.deleteById(new ProfileId(id));
        
        // Flush and clear the persistence context to ensure the delete is visible in subsequent queries within the same transaction
        entityManager.flush();
        entityManager.clear();

        // Then
        assertThat(loadProfilePort.findById(new ProfileId(id))).isEmpty();
    }
}

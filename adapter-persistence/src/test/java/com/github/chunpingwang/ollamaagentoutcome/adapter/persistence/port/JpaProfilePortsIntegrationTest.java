package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.port;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;
import java.util.UUID;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import com.github.chunpingwang.ollamaagentoutcome.domain.profile.Bio;
import com.github.chunpingwang.ollamaagentoutcome.domain.profile.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.profile.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.profile.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.profile.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.profile.exception.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.PersistenceTestApplication;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.repository.JpaProfileRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional // Ensures each test method runs in its own transaction that rolls back automatically
@Import({ JpaLoadProfilePort.class, JpaSaveProfilePort.class, JpaRemoveProfilePort.class })
class JpaProfilePortsIntegrationTest {

    @Autowired
    private JpaLoadProfilePort loadProfilePort;

    @Autowired
    private JpaSaveProfilePort saveProfilePort;

    @Autowired
    private JpaRemoveProfilePort removeProfilePort;

    @Autowired
    private EntityManager entityManager;

    /**
     * Helper to get EntityManager for testing purposes (flush/clear).
     */
    private EntityManager getEntityManager() {
        return entityManager;
    }

    @Test
    void should_save_and_load_profile() {
        // given
        UUID id = UUID.randomUUID();
        Profile profile = new Profile(
            id,
            Email.of("test@example.com"),
            DisplayName.of("Tester"),
            Bio.of("Hello World")
        );

        // when
        Profile saved = saveProfilePort.save(profile);
        
        Optional<Profile> loaded = loadProfilePort.findById(new ProfileId(id));

        // then
        assertThat(loaded).isPresent();
        Profile found = loaded.get();
        assertThat(found.getId()).isEqualTo(id);
        assertThat(found.getEmail().address()).isEqualTo("test@example.com");
        assertThat(found.getDisplayName().name()).isEqualTo("Tester");
        assertThat(found.getBio().content()).isEqualTo("Hello World");
    }

    @Test
    void should_delete_profile_by_id() {
        // given
        UUID id = UUID.randomUUID();
        Profile profile = new Profile(
            id,
            Email.of("delete@example.com"),
            DisplayName.of("Delete Me"),
            Bio.of(null)
        );
        
        saveProfilePort.save(profile);
        
        // when
        removeProfilePort.deleteById(new ProfileId(id));
        getEntityManager().flush();
        getEntityManager().clear();

        // then
        Optional<Profile> deleted = loadProfilePort.findById(new ProfileId(id));
        assertThat(deleted).isEmpty();
    }

    @Test
    void should_throw_duplicate_email_exception_on_violation() {
        // given
        String email = "unique@example.com";
        Profile first = new Profile(
            UUID.randomUUID(),
            Email.of(email),
            DisplayName.of("First"),
            Bio.of(null)
        );
        
        saveProfilePort.save(first);

        Profile second = new Profile(
            UUID.randomUUID(),
            Email.of(email),
            DisplayName.of("Second"),
            Bio.of(null)
        );

        // when & then
        // The duplicate key violation occurs during flush of the second insert.
        // We catch it and assert the correct domain exception is thrown.
        // Note: Do not query DB after this assertion in the same transaction, 
        // as Postgres marks the whole transaction aborted on constraint violations until rollback/commit.
        assertThatThrownBy(() -> saveProfilePort.save(second))
            .isInstanceOf(DuplicateEmailException.class)
            .hasMessageContaining("Duplicate");
            
        // Verification happens automatically via test isolation (rollback at end of method).
    }
    
    @Test
    void should_handle_null_bio_correctly() {
        // given
        UUID id = UUID.randomUUID();
        Profile profile = new Profile(
            id,
            Email.of("nobio@example.com"),
            DisplayName.of("No Bio User"),
            Bio.of(null)
        );

        // when
        Profile saved = saveProfilePort.save(profile);
        

        Optional<Profile> loaded = loadProfilePort.findById(new ProfileId(id));

        // then
        assertThat(loaded).isPresent();
        assertThat(loaded.get().getBio()).isNull();
    }
}

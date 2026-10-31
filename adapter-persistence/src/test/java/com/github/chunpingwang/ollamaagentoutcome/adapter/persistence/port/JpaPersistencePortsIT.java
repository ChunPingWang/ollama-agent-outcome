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
import org.springframework.dao.DataIntegrityViolationException;

import com.github.chunpingwang.ollamaagentoutcome.domain.model.Bio;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.service.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.service.ProfileNotFoundException;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({ JpaSaveProfilePort.class, JpaLoadProfilePort.class, JpaDeleteProfilePort.class })
class JpaPersistencePortsIT {

    @Autowired
    private JpaSaveProfilePort saveProfilePort;

    @Autowired
    private JpaLoadProfilePort loadProfilePort;

    @Autowired
    private JpaDeleteProfilePort deleteProfilePort;

    // Use standard JPA EntityManager to avoid missing class issues in Spring Boot 4 test autoconfigure
    @Autowired
    private EntityManager entityManager;

    @Test
    void should_save_and_load_profile() {
        // Given
        UUID id = UUID.randomUUID();
        Profile profile = new Profile(
                new ProfileId(id),
                new Email("test@example.com"),
                new DisplayName("Tester"),
                new Bio("Hello World")
        );

        // When
        saveProfilePort.save(profile);
        entityManager.flush();
        entityManager.clear();

        Optional<Profile> loaded = loadProfilePort.findById(id);

        // Then
        assertThat(loaded).isPresent().get()
                .satisfies(p -> {
                    assertThat(p.id().value()).isEqualTo(id);
                    assertThat(p.email().address()).isEqualTo("test@example.com");
                    assertThat(p.displayName().name()).isEqualTo("Tester");
                    assertThat(p.bio().content()).isEqualTo("Hello World");
                });
    }

    @Test
    void should_throw_duplicate_email_exception_on_duplicate_insert() {
        // Given
        UUID id1 = UUID.randomUUID();
        Profile p1 = new Profile(new ProfileId(id1), new Email("dup@test.com"), new DisplayName("User 1"), new Bio("Bio"));
        
        UUID id2 = UUID.randomUUID();
        Profile p2 = new Profile(new ProfileId(id2), new Email("dup@test.com"), new DisplayName("User 2"), new Bio("Bio"));

        saveProfilePort.save(p1);
        entityManager.flush();
        entityManager.clear();

        // When & Then
        assertThatThrownBy(() -> saveProfilePort.save(p2))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("email already exists: dup@test.com");
    }

    @Test
    void should_delete_profile_by_id() {
        // Given
        UUID id = UUID.randomUUID();
        Profile profile = new Profile(
                new ProfileId(id),
                new Email("delete@example.com"),
                new DisplayName("To Delete"),
                new Bio("Will be gone")
        );
        saveProfilePort.save(profile);
        entityManager.flush();
        entityManager.clear();

        // When
        deleteProfilePort.deleteById(id);
        entityManager.flush();
        entityManager.clear();

        // Then
        Optional<Profile> loaded = loadProfilePort.findById(id);
        assertThat(loaded).isEmpty();
    }

    @Test
    void should_throw_exception_when_deleting_non_existent_profile() {
        // Given
        UUID nonExistentId = UUID.randomUUID();

        // When & Then
        assertThatThrownBy(() -> deleteProfilePort.deleteById(nonExistentId))
                .isInstanceOf(ProfileNotFoundException.class)
                .hasMessageContaining("profile not found with id: " + nonExistentId);
    }
}

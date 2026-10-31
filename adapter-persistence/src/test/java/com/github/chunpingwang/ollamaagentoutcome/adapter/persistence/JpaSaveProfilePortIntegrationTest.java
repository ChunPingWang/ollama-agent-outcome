package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;



import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

/**
 * 整合測試：驗證 JPA Port 實作與 PostgreSQL Testcontainer 的互動。
 */
// 每個測試後重建 context，避免交易狀態干擾
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JpaSaveProfilePort.class, JpaLoadProfilePort.class, JpaDeleteProfilePort.class})
class JpaSaveProfilePortIntegrationTest {

    @Autowired private JpaSaveProfilePort saveProfilePort;
    @Autowired private JpaLoadProfilePort loadProfilePort;
    @Autowired private JpaDeleteProfilePort deleteProfilePort;
    @Autowired private org.springframework.boot.jpa.test.autoconfigure.TestEntityManager entityManager;

    @Test
    void should_save_and_load_profile() {
        // Given: A new profile to be saved
        var id = UUID.randomUUID();
        Profile profile = Profile.create(
                new ProfileId(id),
                new Name("Alice"),
                new Email("alice@example.com")
        );

        // When: Save the profile via port
        Profile saved = saveProfilePort.save(profile);
        
        // Flush and clear cache to ensure subsequent reads hit DB or cleared state
        entityManager.flush();
        entityManager.clear();

        // Then: Load it back by ID
        assertThat(loadProfilePort.findById(new ProfileId(id)))
                .isPresent()
                .hasValueSatisfying(p -> {
                    assertThat(p.id()).isEqualTo(new ProfileId(id));
                    assertThat(p.name().value()).isEqualTo("Alice");
                    assertThat(p.email().value()).isEqualTo("alice@example.com");
                });
    }

    /**
     * 唯一性衝突測試必須在獨立交易中執行，避免 PostgreSQL 交易中止影響後續查詢。
     */
    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED)
    @Test
    void should_throw_duplicate_email_exception_on_insert_conflict() {
        // Given: First user with specific email
        var id1 = UUID.randomUUID();
        Profile p1 = Profile.create(
                new ProfileId(id1),
                new Name("User 1"),
                new Email("unique@test.com")
        );
        saveProfilePort.save(p1);
        entityManager.flush();
        entityManager.clear();

        // When & Then: Try to insert another user with same email, expect DuplicateEmailException
        var id2 = UUID.randomUUID();
        Profile p2 = Profile.create(
                new ProfileId(id2),
                new Name("User 2"),
                new Email("unique@test.com")
        );

        assertThatThrownBy(() -> saveProfilePort.save(p2))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("email already exists");
        
        // Verify first profile still exists and second was not saved
        entityManager.flush();
        entityManager.clear();
        assertThat(loadProfilePort.findById(new ProfileId(id1))).isPresent();
        assertThat(loadProfilePort.findById(new ProfileId(id2))).isEmpty();
    }

    @Test
    void should_delete_profile_by_id() {
        // Given: A profile to be deleted
        var id = UUID.randomUUID();
        Profile profile = Profile.create(
                new ProfileId(id),
                new Name("Bob"),
                new Email("bob@example.com")
        );
        saveProfilePort.save(profile);
        entityManager.flush();
        entityManager.clear();

        // When: Delete the profile by ID
        deleteProfilePort.deleteById(new ProfileId(id));
        
        // Flush and clear cache to ensure subsequent reads reflect deletion
        entityManager.flush();
        entityManager.clear();

        // Then: Load it back, expect empty
        assertThat(loadProfilePort.findById(new ProfileId(id))).isEmpty();
    }

    @Test
    void should_throw_not_found_exception_when_deleting_non_existent_profile() {
        // Given: Non-existent ID
        var nonExistentId = UUID.randomUUID();

        // When & Then: Try to delete, expect ProfileNotFoundException
        assertThatThrownBy(() -> deleteProfilePort.deleteById(new ProfileId(nonExistentId)))
                .isInstanceOf(ProfileNotFoundException.class)
                .hasMessageContaining("profile not found");
    }
}

package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;



import org.springframework.test.annotation.DirtiesContext;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * Integration tests for JPA Port implementations using Testcontainers PostgreSQL.
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JpaSaveProfilePort.class, JpaLoadProfilePort.class, JpaRemoveProfilePort.class})
class JpaPortIntegrationTest {

    @Autowired
    private JpaSaveProfilePort saveProfilePort;

    @Autowired
    private JpaLoadProfilePort loadProfilePort;

    @Autowired
    private JpaRemoveProfilePort removeProfilePort;

    @Autowired
    private org.springframework.boot.jpa.test.autoconfigure.TestEntityManager entityManager;

    /**
     * 測試成功建立個人資料並能正確加載。
     */
    @Test
    void should_save_and_load_profile() {
        // Arrange
        UUID id = UUID.randomUUID();
        Profile profile = new Profile(
            new ProfileId(id),
            new Name("John Doe"),
            new Email("john@example.com")
        );

        // Act
        ProfileId savedId = saveProfilePort.save(profile);
        
        // Flush and clear to ensure data is persisted in DB and cache is cleared
        entityManager.flush();
        entityManager.clear();

        // Assert
        assertThat(savedId).isEqualTo(new ProfileId(id));
        
        Optional<Profile> loaded = loadProfilePort.findById(new ProfileId(id));
        assertThat(loaded).isPresent();
        assertThat(loaded.get().name().value()).isEqualTo("John Doe");
        assertThat(loaded.get().email().value()).isEqualTo("john@example.com");
    }

    /**
     * 測試更新現有個人資料。
     * 注意：Save Port 應識別為已存在實體並執行 UPDATE，而非 INSERT。
     */
    @Test
    void should_update_existing_profile() {
        // Arrange: Create initial profile
        UUID id = UUID.randomUUID();
        Profile original = new Profile(
            new ProfileId(id),
            new Name("Old Name"),
            new Email("old@example.com")
        );
        saveProfilePort.save(original);
        entityManager.flush();
        entityManager.clear();

        // Prepare updated profile (same ID, different values)
        Profile updated = new Profile(
            new ProfileId(id),
            new Name("New Name"),
            new Email("new@example.com")
        );

        // Act
        ProfileId savedId = saveProfilePort.save(updated);
        
        entityManager.flush();
        entityManager.clear();

        // Assert
        Optional<Profile> loaded = loadProfilePort.findById(new ProfileId(id));
        assertThat(loaded).isPresent();
        assertThat(loaded.get().name().value()).isEqualTo("New Name");
        assertThat(loaded.get().email().value()).isEqualTo("new@example.com");
    }

    /**
     * 測試當電子郵件重複時，Save Port 拋出 Domain Exception DuplicateEmailException。
     * 這是唯一性約束違反的關鍵測試。
     */
    @Test
    void should_throw_duplicate_email_exception_on_save() {
        // Arrange: Create first profile with specific email
        UUID id1 = UUID.randomUUID();
        String sharedEmail = "unique@test.com";
        Profile p1 = new Profile(
            new ProfileId(id1),
            new Name("User One"),
            new Email(sharedEmail)
        );
        saveProfilePort.save(p1);
        entityManager.flush();
        entityManager.clear();

        // Prepare second profile with SAME email but different ID
        UUID id2 = UUID.randomUUID();
        Profile p2 = new Profile(
            new ProfileId(id2),
            new Name("User Two"),
            new Email(sharedEmail)
        );

        // Act & Assert
        // The port implementation must catch DataIntegrityViolationException and throw DuplicateEmailException.
        // This MUST be the last assertion in this method because DB transaction is aborted after constraint violation.
        assertThatThrownBy(() -> saveProfilePort.save(p2))
            .isInstanceOf(DuplicateEmailException.class)
            .hasMessageContaining("email already exists");
            
        // Note: We cannot verify database state here (e.g., count of records) 
        // because PostgreSQL aborts the current transaction on integrity violation.
    }

    /**
     * 測試成功刪除個人資料。
     */
    @Test
    void should_delete_profile_by_id() {
        // Arrange
        UUID id = UUID.randomUUID();
        Profile profile = new Profile(
            new ProfileId(id),
            new Name("To Be Deleted"),
            new Email("delete@example.com")
        );
        saveProfilePort.save(profile);
        entityManager.flush();
        entityManager.clear();

        // Verify it exists first
        assertThat(loadProfilePort.findById(new ProfileId(id))).isPresent();

        // Act
        removeProfilePort.deleteById(new ProfileId(id));
        
        entityManager.flush();
        entityManager.clear();

        // Assert
        Optional<Profile> loaded = loadProfilePort.findById(new ProfileId(id));
        assertThat(loaded).isEmpty();
    }

    /**
     * 測試加載不存在的資料時返回空 Optional，而非拋出異常（由 Application Service 層處理 NotFoundException）。
     */
    @Test
    void should_return_empty_when_loading_non_existent_profile() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();

        // Act & Assert
        Optional<Profile> result = loadProfilePort.findById(new ProfileId(nonExistentId));
        assertThat(result).isEmpty();
    }
}

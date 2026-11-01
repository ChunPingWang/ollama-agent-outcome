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
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;

/**
 * 驗證 JPA Entity mapping、唯一性約束與 Port 實作的 CRUD 行為。
 */
// 測試使用 TestEntityManager（需要交易）：保留測試交易，以 flush()/clear() 控制一級快取；每個測試後重建 context
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JpaSaveProfilePort.class, JpaLoadProfilePort.class, JpaDeleteProfilePort.class})
class PersistenceIntegrationTest {

    @Autowired private SaveProfilePort saveProfilePort;
    @Autowired private LoadProfilePort loadProfilePort;
    @Autowired private DeleteProfilePort deleteProfilePort;
    @Autowired private TestEntityManager entityManager;

    @Test
    void should_save_and_load_profile() {
        // Arrange
        UUID id = UUID.randomUUID();
        Profile profile = new Profile(new ProfileId(id), new Email("test@example.com"), new DisplayName("User"));

        // Act & Assert: Save
        Profile saved = saveProfilePort.save(profile);
        assertThat(saved).isNotNull();
        
        entityManager.flush();
        entityManager.clear();

        // Verify via LoadPort (比較內容而非物件參考，因為持久化會建立新實體)
        Optional<Profile> found = loadProfilePort.findById(new ProfileId(id));
        assertThat(found).isPresent();
        Profile loaded = found.get();
        
        assertThat(loaded.getId()).isEqualTo(saved.getId());
        assertThat(loaded.getEmail().value()).isEqualTo(saved.getEmail().value());
        assertThat(loaded.getDisplayName().value()).isEqualTo(saved.getDisplayName().value());
    }

    @Test
    void should_throw_duplicate_email_exception_on_duplicate_insert() {
        // Arrange
        UUID id1 = UUID.randomUUID();
        Profile p1 = new Profile(new ProfileId(id1), new Email("dup@test.com"), new DisplayName("First"));
        
        UUID id2 = UUID.randomUUID();
        Profile p2 = new Profile(new ProfileId(id2), new Email("dup@test.com"), new DisplayName("Second"));

        // Act & Assert: First insert succeeds
        saveProfilePort.save(p1);
        entityManager.flush();
        entityManager.clear();

        // Second insert must throw domain exception (wrapped from DataIntegrityViolationException)
        // This MUST be the last line of this test method to avoid transaction abort issues.
        assertThatThrownBy(() -> saveProfilePort.save(p2))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("email already exists");
    }

    @Test
    void should_delete_profile_by_id() {
        // Arrange
        UUID id = UUID.randomUUID();
        Profile profile = new Profile(new ProfileId(id), new Email("delete@example.com"), new DisplayName("To Delete"));
        
        saveProfilePort.save(profile);
        entityManager.flush();
        entityManager.clear();

        // Act
        deleteProfilePort.deleteById(new ProfileId(id));
        entityManager.flush();
        entityManager.clear();

        // Assert
        assertThat(loadProfilePort.findById(new ProfileId(id))).isEmpty();
    }
}

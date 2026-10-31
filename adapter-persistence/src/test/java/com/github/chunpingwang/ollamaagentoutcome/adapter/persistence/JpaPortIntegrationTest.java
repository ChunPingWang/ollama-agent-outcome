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
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 測試 Persistence Adapter 中 Port 實作的 CRUD 行為與約束處理。
 */
// 測試使用 TestEntityManager（需要交易）：保留測試交易，以 flush()/clear() 控制一級快取；每個測試後重建 context
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({ JpaSaveProfilePort.class, JpaLoadProfilePort.class, JpaDeleteProfilePort.class })
class JpaPortIntegrationTest {

    @Autowired
    private JpaSaveProfilePort saveProfilePort;

    @Autowired
    private JpaLoadProfilePort loadProfilePort;

    @Autowired
    private JpaDeleteProfilePort deleteProfilePort;

    @Autowired
    private org.springframework.boot.jpa.test.autoconfigure.TestEntityManager entityManager;

    @Test
    void should_save_and_load_profile_successfully() {
        String id = UUID.randomUUID().toString();
        Profile profile = new Profile(
            new ProfileId(id),
            new Name("王小明"),
            new Email("test@example.com")
        );

        // Save
        saveProfilePort.save(profile);
        entityManager.flush();
        entityManager.clear();

        // Load
        var loadedOpt = loadProfilePort.findById(id);
        
        assertThat(loadedOpt).isPresent();
        Profile loaded = loadedOpt.get();
        assertThat(loaded.id()).isEqualTo(new ProfileId(id));
        assertThat(loaded.name().text()).isEqualTo("王小明");
        assertThat(loaded.email().address()).isEqualTo("test@example.com");
    }

    @Test
    void should_throw_duplicate_email_exception_when_saving_existing_email() {
        String id1 = UUID.randomUUID().toString();
        Profile p1 = new Profile(
            new ProfileId(id1),
            new Name("User 1"),
            new Email("unique@test.com")
        );
        saveProfilePort.save(p1);
        entityManager.flush();
        entityManager.clear();

        String id2 = UUID.randomUUID().toString();
        Profile p2 = new Profile(
            new ProfileId(id2),
            new Name("User 2"),
            new Email("unique@test.com") // Duplicate email
        );

        // This must be the last assertion in this test method because DB transaction is aborted on constraint violation.
        assertThatThrownBy(() -> saveProfilePort.save(p2))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("電子郵件已重複");
        
        // Verify first record still exists (conceptually, though we can't query after abort easily in same tx without commit/rollback logic handled by framework)
        // We rely on the fact that if it didn't throw, it would have saved. Since it threw, it failed.
    }

    @Test
    void should_delete_profile_by_id() {
        String id = UUID.randomUUID().toString();
        Profile profile = new Profile(
            new ProfileId(id),
            new Name("To Delete"),
            new Email("delete@example.com")
        );

        saveProfilePort.save(profile);
        entityManager.flush();
        entityManager.clear();

        deleteProfilePort.deleteById(id);
        entityManager.flush();
        entityManager.clear();

        var loadedOpt = loadProfilePort.findById(id);
        assertThat(loadedOpt).isEmpty();
    }

    @Test
    void should_throw_exception_when_deleting_non_existent_profile() {
        String nonExistentId = "non-existent-id";

        assertThatThrownBy(() -> deleteProfilePort.deleteById(nonExistentId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("找不到識別碼為 " + nonExistentId);
    }
}

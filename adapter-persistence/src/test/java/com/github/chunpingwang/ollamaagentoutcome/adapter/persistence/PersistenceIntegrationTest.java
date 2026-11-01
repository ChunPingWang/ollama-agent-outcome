package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;


import org.springframework.test.annotation.DirtiesContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;
import java.util.UUID;

import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

/**
 * 驗證 Persistence Adapter 的 Entity Mapping、唯一性約束與 Port 行為。
 */
// 測試使用 TestEntityManager（需要交易）：保留測試交易，以 flush()/clear() 控制一級快取；每個測試後重建 context
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JpaSaveProfilePort.class, JpaLoadProfilePort.class, JpaDeleteProfilePort.class})
class PersistenceIntegrationTest {

    @Autowired private JpaSaveProfilePort saveProfilePort;
    @Autowired private JpaLoadProfilePort loadProfilePort;
    @Autowired private JpaDeleteProfilePort deleteProfilePort;
    @Autowired private org.springframework.boot.jpa.test.autoconfigure.TestEntityManager entityManager;

    /**
     * 測試建立個人資料並儲存成功，且能正確載入。
     */
    @Test
    void should_save_and_load_profile() {
        UUID id = UUID.randomUUID();
        String email = "test@example.com";
        String displayName = "Test User";

        var saved = saveProfilePort.save(new com.github.chunpingwang.ollamaagentoutcome.domain.Profile(
            new com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId(id), 
            new com.github.chunpingwang.ollamaagentoutcome.domain.Email(email), 
            new com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName(displayName)
        ));

        // 確保資料寫入資料庫
        entityManager.flush();
        entityManager.clear();

        Optional<com.github.chunpingwang.ollamaagentoutcome.domain.Profile> loadedOpt = loadProfilePort.findById(saved.getId());
        assertThat(loadedOpt).isPresent();
        com.github.chunpingwang.ollamaagentoutcome.domain.Profile profile = loadedOpt.get();
        
        assertThat(profile.getId()).isEqualTo(new com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId(id));
        assertThat(profile.getEmail().getValue()).isEqualTo(email.toLowerCase());
        assertThat(profile.getDisplayName().value()).isEqualTo(displayName);
    }

    /**
     * 測試重複電子郵件應拋出 DataIntegrityViolationException（由 Port 轉為 DuplicateEmailException）。
     * 
     * 注意：此斷言必須是該方法的最後一行，因為 PostgreSQL 在約束違反後會中止交易。
     */
    @Test
    void should_throw_duplicate_email_exception_on_duplicate_email() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        String duplicateEmail = "dup@test.com";

        // 建立第一筆資料
        saveProfilePort.save(new com.github.chunpingwang.ollamaagentoutcome.domain.Profile(
            new com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId(id1), 
            new com.github.chunpingwang.ollamaagentoutcome.domain.Email(duplicateEmail), 
            new com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName("User One")
        ));
        
        // 嘗試建立第二筆相同 Email 的資料
        assertThatThrownBy(() -> saveProfilePort.save(new com.github.chunpingwang.ollamaagentoutcome.domain.Profile(
                new com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId(id2), 
                new com.github.chunpingwang.ollamaagentoutcome.domain.Email(duplicateEmail), 
                new com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName("User Two")
            )))
                .isInstanceOf(com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException.class)
                .hasMessageContaining("duplicate");
    }

    /**
     * 測試刪除個人資料成功，且隨後查詢不到該資料。
     */
    @Test
    void should_delete_profile_by_id() {
        var saved = saveProfilePort.save(new com.github.chunpingwang.ollamaagentoutcome.domain.Profile(
            new com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId(UUID.randomUUID()), 
            new com.github.chunpingwang.ollamaagentoutcome.domain.Email("delete@example.com"), 
            new com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName("Delete Me")
        ));
        entityManager.flush();
        entityManager.clear();

        deleteProfilePort.deleteById(saved.getId());
        entityManager.flush();
        entityManager.clear();

        assertThat(loadProfilePort.findById(saved.getId())).isEmpty();
    }
}

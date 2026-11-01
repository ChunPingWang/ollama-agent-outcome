package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;


import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import org.springframework.test.annotation.DirtiesContext;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

/**
 * Persistence Adapter 整合測試：驗證 Entity Mapping、唯一性約束與 Port 行為。
 */
// 測試使用 TestEntityManager（需要交易）：保留測試交易，以 flush()/clear() 控制一級快取；每個測試後重建 context
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JpaSaveProfilePort.class, JpaLoadProfilePort.class, JpaDeleteProfilePort.class, ProfileEntityConverter.class})
class PersistenceIntegrationTest {

    @Autowired private JpaSaveProfilePort saveProfilePort;
    @Autowired private JpaLoadProfilePort loadProfilePort;
    @Autowired private JpaDeleteProfilePort deleteProfilePort;
    @Autowired private org.springframework.boot.jpa.test.autoconfigure.TestEntityManager entityManager;

    /**
     * 測試建立個人資料並成功儲存。
     */
    @Test
    void should_save_and_load_profile() {
        UUID id = UUID.randomUUID();
        
        // 執行保存
        var saved = saveProfilePort.save(new com.github.chunpingwang.ollamaagentoutcome.domain.Profile(
                new com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId(id),
                new com.github.chunpingwang.ollamaagentoutcome.domain.Email("test@example.com"),
                new com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName("Tester")
        ));

        // 刷新並清除快取，確保從資料庫讀取
        entityManager.flush();
        entityManager.clear();

        // 驗證載入結果
        assertThat(loadProfilePort.findById(saved.getId())).isPresent().get().satisfies(profile -> {
            assertThat(profile.getEmail().value()).isEqualTo("test@example.com");
            assertThat(profile.getDisplayName().value()).isEqualTo("Tester");
        });
    }

    /**
     * 測試重複電子郵件應拋出 DataIntegrityViolationException（由 Port 轉為 DuplicateEmailException）。
     * 
     * 注意：此斷言必須是該方法的最後一行，因為 PostgreSQL 在約束違反後會中止交易。
     */
    @Test
    void should_throw_duplicate_email_exception_on_duplicate() {
        UUID id1 = UUID.randomUUID();
        
        saveProfilePort.save(new com.github.chunpingwang.ollamaagentoutcome.domain.Profile(
                new com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId(id1),
                new com.github.chunpingwang.ollamaagentoutcome.domain.Email("unique@test.com"),
                new com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName("First")
        ));
        entityManager.flush();
        entityManager.clear();

        UUID id2 = UUID.randomUUID();
        
        // 嘗試儲存相同 Email，預期失敗
        assertThatThrownBy(() -> saveProfilePort.save(new com.github.chunpingwang.ollamaagentoutcome.domain.Profile(
                new com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId(id2),
                new com.github.chunpingwang.ollamaagentoutcome.domain.Email("unique@test.com"),
                new com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName("Second")
        )))
            .isInstanceOf(com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException.class);
    }

    /**
     * 測試刪除個人資料。
     */
    @Test
    void should_delete_profile_by_id() {
        UUID id = UUID.randomUUID();
        
        var saved = saveProfilePort.save(new com.github.chunpingwang.ollamaagentoutcome.domain.Profile(
                new com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId(id),
                new com.github.chunpingwang.ollamaagentoutcome.domain.Email("delete@example.com"),
                new com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName("To Be Deleted")
        ));
        
        entityManager.flush();
        entityManager.clear();

        deleteProfilePort.deleteById(saved.getId());
        
        // 刷新並清除快取，確保從資料庫讀取（確認已刪除）
        entityManager.flush();
        entityManager.clear();

        assertThat(loadProfilePort.findById(saved.getId())).isEmpty();
    }
}

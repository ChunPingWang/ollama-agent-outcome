package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;






import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
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
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * JPA Save Profile Port 整合測試：驗證新增（isNew=true）與更新（merge）邏輯，以及 DataIntegrityViolationException 轉為 DuplicateEmailException。
 */
// 每個測試後重建 context，避免交易與快取干擾
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JpaSaveProfilePort.class, JpaLoadProfilePort.class})
class JpaSaveProfilePortTest {

    @Autowired
    private JpaSaveProfilePort saveProfilePort;

    @Autowired
    private JpaLoadProfilePort loadProfilePort;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void should_save_new_profile() {
        UUID id = UUID.randomUUID();
        String email = "new@example.com";
        String displayName = "New User";

        com.github.chunpingwang.ollamaagentoutcome.domain.Profile profile = com.github.chunpingwang.ollamaagentoutcome.domain.Profile.create(
            new com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId(id),
            new com.github.chunpingwang.ollamaagentoutcome.domain.Email(email),
            new com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName(displayName)
        );

        // 儲存新資料
        saveProfilePort.save(profile);
        
        // 驗證交易內已寫入且可查詢到
        Optional<com.github.chunpingwang.ollamaagentoutcome.domain.Profile> loaded = loadProfilePort.findById(id);
        assertThat(loaded).isPresent();
        assertThat(loaded.get().email().value()).isEqualTo(email);
    }

    @Test
    void should_update_existing_profile() {
        UUID id = UUID.randomUUID();
        String originalEmail = "original@example.com";
        String updatedDisplayName = "Updated Name";

        // 先建立一筆現有資料
        com.github.chunpingwang.ollamaagentoutcome.domain.Profile existing = com.github.chunpingwang.ollamaagentoutcome.domain.Profile.create(
            new ProfileId(id),
            new Email(originalEmail),
            new DisplayName("Old Name")
        );
        saveProfilePort.save(existing);
        entityManager.flush();
        entityManager.clear();

        // 更新顯示名稱（假設 Port 支援透過 ID 找到舊資料並合併，或此測試僅驗證能成功儲存不拋異常）
        // 注意：根據 T5 說明，SavePort 需處理 isNew。這裡模擬一個帶有相同 ID 但不同內容的 Profile 進行「更新」操作。
        // 由於 Domain Object 本身無 isNew 狀態，JpaSaveProfilePort 內部應檢查 DB 是否存在該 ID。
        com.github.chunpingwang.ollamaagentoutcome.domain.Profile toUpdate = com.github.chunpingwang.ollamaagentoutcome.domain.Profile.create(
            new ProfileId(id),
            new Email(originalEmail), // Email 不變
            new DisplayName(updatedDisplayName)
        );

        saveProfilePort.save(toUpdate);
        
        Optional<com.github.chunpingwang.ollamaagentoutcome.domain.Profile> loaded = loadProfilePort.findById(id);
        assertThat(loaded).isPresent();
        assertThat(loaded.get().displayName().value()).isEqualTo(updatedDisplayName);
    }

    @Test
    void should_throw_duplicate_email_exception_on_violation() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        String duplicateEmail = "dup@example.com";

        // 建立第一筆資料
        com.github.chunpingwang.ollamaagentoutcome.domain.Profile first = com.github.chunpingwang.ollamaagentoutcome.domain.Profile.create(
            new ProfileId(id1),
            new Email(duplicateEmail),
            new DisplayName("First")
        );
        saveProfilePort.save(first);
        
        // 嘗試儲存第二筆相同 email，應拋出 DuplicateEmailException (Domain Exception)
        com.github.chunpingwang.ollamaagentoutcome.domain.Profile second = com.github.chunpingwang.ollamaagentoutcome.domain.Profile.create(
            new ProfileId(id2),
            new Email(duplicateEmail),
            new DisplayName("Second")
        );
        
        // DataIntegrityViolationException 轉為 Domain DuplicateEmailException
        assertThatThrownBy(() -> saveProfilePort.save(second))
            .isInstanceOf(DuplicateEmailException.class)
            .hasMessageContaining("email already exists");
    }
}

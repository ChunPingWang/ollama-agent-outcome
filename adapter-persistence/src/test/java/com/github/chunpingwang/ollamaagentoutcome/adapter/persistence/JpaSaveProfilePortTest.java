package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;




import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import static org.assertj.core.api.Assertions.*;import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;

// 測試使用 TestEntityManager（需要交易）：保留測試交易，以 flush()/clear() 控制一級快取；每個測試後重建 context
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({ JpaSaveProfilePort.class })
class JpaSaveProfilePortTest {

    @Autowired
    private SaveProfilePort saveProfilePort;

    @Autowired
    private org.springframework.boot.jpa.test.autoconfigure.TestEntityManager entityManager;

    @Test
    void should_save_and_load_profile() {
        UUID id = UUID.randomUUID();
        Email email = new Email("test@example.com");
        DisplayName displayName = new DisplayName("Tester");
        Profile profile = new Profile(id, email, displayName);

        Profile saved = saveProfilePort.save(profile);

        // 確保資料寫入資料庫並清除快取，以便後續查詢能讀取最新狀態
        entityManager.flush();
        entityManager.clear();

        assertThat(saved.id()).isEqualTo(id);
        assertThat(saved.email().value()).isEqualTo("test@example.com");
        assertThat(saved.displayName().value()).isEqualTo("Tester");
        
        // 驗證可以透過 ID 載回相同的資料
        var loaded = entityManager.find(ProfileEntity.class, id);
        assertThat(loaded).isNotNull();
        assertThat(loaded.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void should_throw_duplicate_exception_when_email_already_exists() {
        UUID id1 = UUID.randomUUID();
        Email email = new Email("unique@example.com");
        DisplayName displayName = new DisplayName("User One");
        Profile profile1 = new Profile(id1, email, displayName);

        // 先儲存第一筆成功
        saveProfilePort.save(profile1);
        entityManager.flush();
        entityManager.clear();

        // 嘗試儲存相同電子郵件的第二筆，應拋出 DuplicateEmailException
        UUID id2 = UUID.randomUUID();
        Profile profile2 = new Profile(id2, email, new DisplayName("User Two"));

        // PostgreSQL 在唯一約束衝突後會中止交易 (transaction aborted)。
        // 若在同一個測試交易區塊內繼續操作 EntityManager，後續查詢可能返回 null 或拋出 SQL 異常。
        // 因此此斷言必須是該方法的最後一個動作，且不在同一交易內進行額外查詢。
        assertThatThrownBy(() -> saveProfilePort.save(profile2))
                .isInstanceOf(com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException.class)
                .hasMessageContaining("Duplicate email");
        
        // 注意：這裡不能再做 entityManager.find() 或其他斷言，因為交易已因約束違反而中止。
    }
}

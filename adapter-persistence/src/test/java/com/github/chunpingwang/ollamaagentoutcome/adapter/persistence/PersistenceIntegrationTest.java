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

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

/**
 * 驗證 Persistence Adapter 的 Entity Mapping、唯一性約束與 Port 行為。
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({
    JpaSaveProfilePort.class,
    JpaLoadProfilePort.class,
    JpaDeleteProfilePort.class,
    ProfileEntityConverter.class
})
// 測試使用 TestEntityManager（需要交易）：保留測試交易，以 flush()/clear() 控制一級快取；每個測試後重建 context
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PersistenceIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private JpaSaveProfilePort saveProfilePort;

    @Autowired
    private JpaLoadProfilePort loadProfilePort;

    @Autowired
    private JpaDeleteProfilePort deleteProfilePort;

    /**
     * 測試建立個人資料並成功儲存。
     */
    @Test
    void should_save_and_load_profile() {
        UUID id = UUID.randomUUID();
        
        // 透過 Port 儲存（內部會轉換為 Entity）
        var savedProfile = saveProfilePort.save(new com.github.chunpingwang.ollamaagentoutcome.domain.Profile(
            new com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId(id),
            new com.github.chunpingwang.ollamaagentoutcome.domain.Email("test@example.com"),
            new com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName("Test User")
        ));

        // flush & clear 確保寫入資料庫並清除一級快取
        entityManager.flush();
        entityManager.clear();

        // 驗證可以載入
        assertThat(loadProfilePort.findById(savedProfile.getId())).isPresent().get()
                .satisfies(profile -> {
                    assertThat(profile.getEmailValue()).isEqualTo("test@example.com");
                    assertThat(profile.getDisplayNameValue()).isEqualTo("Test User");
                });
    }

    /**
     * 測試重複電子郵件應拋出 Domain Exception (DuplicateEmailException)。
     * 
     * 注意：PostgreSQL 在約束違反後交易中止，因此此斷言必須是該方法的最後一行。
     */
    @Test
    void should_throw_duplicate_email_exception_on_save_with_existing_email() {
        UUID id1 = UUID.randomUUID();
        
        saveProfilePort.save(new com.github.chunpingwang.ollamaagentoutcome.domain.Profile(
            new com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId(id1),
            new com.github.chunpingwang.ollamaagentoutcome.domain.Email("unique@test.com"),
            new com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName("User One")
        ));
        
        entityManager.flush();
        entityManager.clear();

        UUID id2 = UUID.randomUUID();
        
        // 嘗試儲存相同 Email，預期拋出 DuplicateEmailException
        assertThatThrownBy(() -> saveProfilePort.save(new com.github.chunpingwang.ollamaagentoutcome.domain.Profile(
            new com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId(id2),
            new com.github.chunpingwang.ollamaagentoutcome.domain.Email("unique@test.com"),
            new com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName("User Two")
        )))
                .isInstanceOf(com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException.class)
                .hasMessageContaining("duplicate email: unique@test.com");
    }

    /**
     * 測試刪除個人資料。
     */
    @Test
    void should_delete_profile_by_id() {
        UUID id = UUID.randomUUID();
        
        var savedProfile = saveProfilePort.save(new com.github.chunpingwang.ollamaagentoutcome.domain.Profile(
            new com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId(id),
            new com.github.chunpingwang.ollamaagentoutcome.domain.Email("delete@example.com"),
            new com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName("To Be Deleted")
        ));

        entityManager.flush();
        entityManager.clear();

        deleteProfilePort.deleteById(savedProfile.getId());
        
        entityManager.flush();
        entityManager.clear();

        assertThat(loadProfilePort.findById(savedProfile.getId())).isEmpty();
    }
}

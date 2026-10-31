package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;


import org.springframework.test.annotation.DirtiesContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

// 測試使用 TestEntityManager（需要交易）：保留測試交易，以 flush()/clear() 控制一級快取；每個測試後重建 context
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({ JpaSaveProfilePort.class, JpaLoadProfilePort.class })
class JpaSaveProfilePortTest {

    @Autowired
    private JpaSaveProfilePort saveProfilePort;

    @Autowired
    private JpaLoadProfilePort loadProfilePort;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private org.springframework.boot.jpa.test.autoconfigure.TestEntityManager entityManager;

    @Test
    void should_save_profile_successfully() {
        String id = UUID.randomUUID().toString();
        var email = new Email("test@example.com");
        var displayName = new DisplayName("Test User");
        var profile = Profile.create(new ProfileId(id), email, displayName);

        saveProfilePort.save(profile);
        
        // 確保寫入資料庫並清除快取，以便後續查詢能讀取最新狀態
        entityManager.flush();
        entityManager.clear();

        assertThat(entityManager.find(ProfileEntity.class, id)).isNotNull();
        assertThat(loadProfilePort.findById(id).get()).isEqualTo(profile);
    }

    @Test
    void should_throw_duplicate_email_exception_when_saving_existing_email() {
        String id1 = UUID.randomUUID().toString();
        String id2 = UUID.randomUUID().toString();
        var email = new Email("duplicate@test.com");
        var name1 = new DisplayName("User One");
        var name2 = new DisplayName("User Two");
        
        var profile1 = Profile.create(new ProfileId(id1), email, name1);
        var profile2 = Profile.create(new ProfileId(id2), email, name2);

        saveProfilePort.save(profile1);
        entityManager.flush();
        entityManager.clear();

        // 重複儲存相同 email 應拋出 Domain Exception
        assertThatThrownBy(() -> saveProfilePort.save(profile2))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("email already exists");
                
        // 驗證第一筆資料仍然存在（由於交易中止，無法在同一方法內做更多斷言）
    }
}

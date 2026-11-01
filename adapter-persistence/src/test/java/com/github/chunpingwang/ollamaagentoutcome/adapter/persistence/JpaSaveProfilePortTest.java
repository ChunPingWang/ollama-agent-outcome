package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;



import org.springframework.test.annotation.DirtiesContext;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
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
@Import({ JpaSaveProfilePort.class })
class JpaSaveProfilePortTest {

    @Autowired
    private JpaSaveProfilePort saveProfilePort;

    @Autowired
    private org.springframework.boot.jpa.test.autoconfigure.TestEntityManager entityManager;

    @Test
    void should_save_profile_successfully() {
        UUID id = UUID.randomUUID();
        Email email = new Email("test@example.com");
        DisplayName displayName = new DisplayName("Test User");
        
        Profile profile = new Profile(id, email, displayName);

        Profile saved = saveProfilePort.save(profile);
        
        entityManager.flush();
        entityManager.clear();

        assertThat(saved.id()).isEqualTo(new ProfileId(id));
        assertThat(saved.email().value()).isEqualTo(email.value());
        assertThat(saved.displayName().value()).isEqualTo(displayName.value());
    }

    @Test
    void should_throw_duplicate_exception_when_email_exists() {
        UUID id1 = UUID.randomUUID();
        Email email = new Email("unique@example.com");
        DisplayName displayName = new DisplayName("User One");
        
        Profile p1 = new Profile(id1, email, displayName);
        saveProfilePort.save(p1);
        entityManager.flush();
        entityManager.clear(); // 確保快取清除，讓下一筆插入觸發 DB 約束

        UUID id2 = UUID.randomUUID();
        Profile p2 = new Profile(id2, email, new DisplayName("User Two"));

        // DataIntegrityViolationException 必須是該測試的最後一行斷言前最後一個操作
        assertThatThrownBy(() -> saveProfilePort.save(p2))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("already exists");
                
        // 注意：由於交易中止，不能在後面再加任何 flush 或查詢。
        // 驗證第一筆仍存在需在其他測試方法中進行，或使用不同的交易策略（此處依規則不處理跨步驟驗證）。
    }
}

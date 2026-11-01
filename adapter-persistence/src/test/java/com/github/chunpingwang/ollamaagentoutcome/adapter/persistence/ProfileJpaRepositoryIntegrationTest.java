package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;


import org.springframework.test.annotation.DirtiesContext;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

/**
 * 個人資料 Repository 整合測試，驗證 CRUD 與唯一性約束。
 */
// 測試使用 TestEntityManager（需要交易）：保留測試交易，以 flush()/clear() 控制一級快取；每個測試後重建 context
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({SaveProfilePortImpl.class, LoadProfilePortImpl.class, ExistsByEmailPortImpl.class, DeleteProfileByIdPortImpl.class, ProfileConverter.class})
class ProfileJpaRepositoryIntegrationTest {

    @Autowired private SaveProfilePortImpl saveProfilePort;
    @Autowired private LoadProfilePortImpl loadProfilePort;
    @Autowired private TestEntityManager entityManager;

    @Test
    void should_save_and_load_profile() {
        // Given
        String id = UUID.randomUUID().toString();
        var profile = new com.github.chunpingwang.ollamaagentoutcome.domain.Profile(
                id, 
                new Email("test@example.com"), 
                new DisplayName("Tester")
        );

        // When
        saveProfilePort.save(profile);
        entityManager.flush();
        entityManager.clear();

        // Then
        assertThat(loadProfilePort.findById(id)).isPresent();
    }

    @Test
    void should_throw_on_duplicate_email() {
        // Given
        String email = "unique@test.com";
        var p1 = new com.github.chunpingwang.ollamaagentoutcome.domain.Profile(UUID.randomUUID().toString(), new Email(email), new DisplayName("User 1"));
        
        saveProfilePort.save(p1);
        entityManager.flush();
        entityManager.clear();

        var p2 = new com.github.chunpingwang.ollamaagentoutcome.domain.Profile(UUID.randomUUID().toString(), new Email(email), new DisplayName("User 2"));

        // When & Then
        // Port 實作會將 DataIntegrityViolationException 轉換為 Domain Exception，故斷言 DuplicateEmailException
        assertThatThrownBy(() -> saveProfilePort.save(p2))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("電子郵件已存在");
    }
}

package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;



import org.springframework.test.annotation.DirtiesContext;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

// 測試使用 TestEntityManager（需要交易）：保留測試交易，以 flush()/clear() 控制一級快取；每個測試後重建 context
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JpaSaveProfilePort.class)
class JpaSaveProfilePortTest {

    @Autowired
    private JpaSaveProfilePort saveProfilePort;

    @Autowired
    private org.springframework.boot.jpa.test.autoconfigure.TestEntityManager entityManager;

    @Test
    void should_save_profile_successfully() {
        UUID id = UUID.randomUUID();
        Email email = new Email("user@example.com");
        DisplayName displayName = new DisplayName("John Doe");
        
        Profile profile = new Profile(id, email, displayName);
        
        saveProfilePort.save(profile);
        entityManager.flush();
        entityManager.clear();

        // 驗證資料已存入資料庫
        var entity = entityManager.find(ProfileEntity.class, id);
        assertThat(entity).isNotNull();
        assertThat(entity.getEmail()).isEqualTo(email.address());
        assertThat(entity.getDisplayName()).isEqualTo(displayName.name());
    }

    @Test
    void should_throw_duplicate_email_exception_on_constraint_violation() {
        String sharedEmail = "unique@test.com";
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        
        Email email = new Email(sharedEmail);
        DisplayName dn1 = new DisplayName("User One");
        DisplayName dn2 = new DisplayName("User Two");
        
        Profile p1 = new Profile(id1, email, dn1);
        Profile p2 = new Profile(id2, email, dn2);
        
        saveProfilePort.save(p1);
        entityManager.flush();
        
        // 第二筆使用相同的 email，應觸發唯一約束違反並轉換為 DuplicateEmailException
        // 注意：這必須是該方法的最後一個斷言或操作，因為 PostgreSQL 在 constraint violation 後會中止交易
        assertThatThrownBy(() -> saveProfilePort.save(p2))
            .isInstanceOf(DuplicateEmailException.class)
            .hasMessageContaining("Duplicate")
            .hasCauseInstanceOf(DataIntegrityViolationException.class);
    }
}

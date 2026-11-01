package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;



import org.springframework.test.annotation.DirtiesContext;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import java.util.UUID;

// 測試使用 TestEntityManager（需要交易）：保留測試交易，以 flush()/clear() 控制一級快取；每個測試後重建 context
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JpaSaveProfilePortImpl.class, JpaLoadProfilePortImpl.class, JpaRemoveProfilePortImpl.class})
class JpaProfileRepositoryTest {

    @Autowired private JpaSaveProfilePortImpl saveProfilePort;
    @Autowired private JpaLoadProfilePortImpl loadProfilePort;
    @Autowired private JpaRemoveProfilePortImpl removeProfilePort;
    @Autowired private org.springframework.boot.jpa.test.autoconfigure.TestEntityManager entityManager;

    @Test
    void should_save_and_load_profile() {
        UUID id = UUID.randomUUID();
        Email email = new Email("test@example.com");
        DisplayName displayName = new DisplayName("Test User");
        
        saveProfilePort.save(id, email, displayName);
        
        entityManager.flush();
        entityManager.clear();

        assertThat(loadProfilePort.findById(new ProfileId(id.toString()))).isPresent();
    }

    @Test
    void should_throw_duplicate_email_exception_on_duplicate_email() {
        String emailStr = "dup@example.com";
        Email email = new Email(emailStr);
        DisplayName dn1 = new DisplayName("User 1");
        DisplayName dn2 = new DisplayName("User 2");
        
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        saveProfilePort.save(id1, email, dn1);
        entityManager.flush();
        
        // 第二筆使用相同的 email，應觸發唯一約束違反並轉為 Domain Exception
        assertThatThrownBy(() -> saveProfilePort.save(id2, email, dn2))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("電子郵件已存在: dup@example.com");
    }

    @Test
    void should_delete_profile_by_id() {
        UUID id = UUID.randomUUID();
        Email email = new Email("delete@example.com");
        DisplayName displayName = new DisplayName("Delete User");
        
        saveProfilePort.save(id, email, displayName);
        entityManager.flush();
        entityManager.clear();

        removeProfilePort.deleteById(new ProfileId(id.toString()));
        entityManager.flush();
        entityManager.clear();

        assertThat(loadProfilePort.findById(new ProfileId(id.toString()))).isEmpty();
    }
}

package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;



import org.springframework.test.annotation.DirtiesContext;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

// 測試使用 TestEntityManager（需要交易）：保留測試交易，以 flush()/clear() 控制一級快取；每個測試後重建 context
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({ JpaSaveProfilePort.class, JpaExistsByEmailPort.class })
class JpaExistsByEmailPortTest {

    @Autowired
    private JpaSaveProfilePort saveProfilePort;

    @Autowired
    private JpaExistsByEmailPort existsByEmailPort;

    @Autowired
    private org.springframework.boot.jpa.test.autoconfigure.TestEntityManager entityManager;

    @Test
    void should_return_true_when_email_exists() {
        UUID id = UUID.randomUUID();
        Email email = new Email("exists@example.com");
        DisplayName displayName = new DisplayName("Existing User");
        
        Profile profile = new Profile(id, email, displayName);
        saveProfilePort.save(profile);
        
        entityManager.flush();
        entityManager.clear(); // 確保查詢的是 DB 狀態而非快取

        boolean result = existsByEmailPort.existsByEmail(email);

        assertThat(result).isTrue();
    }

    @Test
    void should_return_false_when_email_not_exists() {
        Email nonExistentEmail = new Email("nonexistent@example.com");

        boolean result = existsByEmailPort.existsByEmail(nonExistentEmail);

        assertThat(result).isFalse();
    }
}

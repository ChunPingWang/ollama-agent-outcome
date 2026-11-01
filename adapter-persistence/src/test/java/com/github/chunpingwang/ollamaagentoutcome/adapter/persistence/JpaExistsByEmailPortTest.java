package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;



import org.springframework.test.annotation.DirtiesContext;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.ExistsByEmailPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;

// 測試使用 TestEntityManager（需要交易）：保留測試交易，以 flush()/clear() 控制一級快取；每個測試後重建 context
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JpaSaveProfilePort.class, JpaExistsByEmailPort.class, ProfileEntityConverter.class})
class JpaExistsByEmailPortTest {

    @Autowired
    private ExistsByEmailPort existsByEmailPort;

    @Autowired
    private SaveProfilePort saveProfilePort;

    @Autowired
    private org.springframework.boot.jpa.test.autoconfigure.TestEntityManager entityManager;

    @Test
    void should_return_true_when_email_exists() {
        // Given
        String emailStr = "exists@example.com";
        UUID id1 = UUID.randomUUID();
        
        Profile profile = Profile.create(id1, emailStr, "Existing User");
        saveAndClear(profile);

        // When
        boolean result = existsByEmailPort.existsByEmail(new Email(emailStr));

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void should_return_false_when_email_does_not_exist() {
        // Given: No data inserted for this specific test to ensure isolation
        
        // When
        boolean result = existsByEmailPort.existsByEmail(new Email("nonexistent@example.com"));

        // Then
        assertThat(result).isFalse();
    }

    private void saveAndClear(Profile profile) {
        var saved = saveProfilePort.save(profile);
        entityManager.flush();
        entityManager.clear();
    }
}

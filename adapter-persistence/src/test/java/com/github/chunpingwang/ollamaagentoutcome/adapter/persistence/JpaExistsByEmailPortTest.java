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

import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

// 測試使用 TestEntityManager（需要交易）：保留測試交易，以 flush()/clear() 控制一級快取；每個測試後重建 context
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({ JpaSaveProfilePort.class, JpaExistsByEmailPort.class })
class JpaExistsByEmailPortTest {

    @Autowired private JpaSaveProfilePort saveProfilePort;
    @Autowired private JpaExistsByEmailPort existsByEmailPort;
    @Autowired private org.springframework.boot.jpa.test.autoconfigure.TestEntityManager entityManager;

    @Test
    void should_return_true_when_email_exists() {
        UUID id = UUID.randomUUID();
        Email email = new Email("exists@example.com");
        DisplayName dn = new DisplayName("Existence Check User");
        var profile = new Profile(new ProfileId(id), email, dn);

        saveProfilePort.save(profile);
        entityManager.flush();
        entityManager.clear();

        assertThat(existsByEmailPort.existsByEmail(email)).isTrue();
    }

    @Test
    void should_return_false_when_email_does_not_exist() {
        Email nonExistentEmail = new Email("ghost@example.com");

        assertThat(existsByEmailPort.existsByEmail(nonExistentEmail)).isFalse();
    }
}

package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;


import org.springframework.test.annotation.DirtiesContext;
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
@Import({ JpaSaveProfilePort.class, JpaExistsByEmailPort.class, ProfileEntityConverter.class })
class JpaExistsByEmailPortTest {

    @Autowired
    private JpaSaveProfilePort saveProfilePort;

    @Autowired
    private JpaExistsByEmailPort existsByEmailPort;

    @Autowired
    private org.springframework.boot.jpa.test.autoconfigure.TestEntityManager entityManager;

    @Test
    void should_return_true_when_email_exists() {
        var id = new ProfileId(UUID.randomUUID());
        var email = new Email("exists@example.com");
        var displayName = new DisplayName("Existing User");
        var profile = new Profile(id, email, displayName);

        saveProfilePort.save(profile);
        entityManager.flush();
        entityManager.clear();

        boolean exists = existsByEmailPort.existsByEmail(email);

        assertThat(exists).isTrue();
    }

    @Test
    void should_return_false_when_email_does_not_exist() {
        var nonExistentEmail = new Email("ghost@example.com");

        boolean exists = existsByEmailPort.existsByEmail(nonExistentEmail);

        assertThat(exists).isFalse();
    }
}

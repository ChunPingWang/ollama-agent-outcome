package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;



import static org.assertj.core.api.Assertions.*;import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.ExistsByEmailPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({ JpaSaveProfilePort.class, JpaExistsByEmailPort.class })
class JpaExistsByEmailPortTest {

    @Autowired
    private SaveProfilePort saveProfilePort;

    @Autowired
    private ExistsByEmailPort existsByEmailPort;

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
        entityManager.clear();

        boolean exists = existsByEmailPort.existsByEmail(email);

        assertThat(exists).isTrue();
    }

    @Test
    void should_return_false_when_email_not_exists() {
        Email nonExistentEmail = new Email("ghost@example.com");

        boolean exists = existsByEmailPort.existsByEmail(nonExistentEmail);

        assertThat(exists).isFalse();
    }
}

package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;



import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

// 每次 repository 呼叫各自交易：避免一級快取與交易中止問題；每個測試後重建 context 與 schema
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({ JpaSaveProfilePort.class, ProfileEntityConverter.class, JpaExistsByEmailPort.class })
class JpaExistsByEmailPortTest {

    @Autowired
    private JpaSaveProfilePort saveProfilePort;

    @Autowired
    private JpaExistsByEmailPort existsByEmailPort;

    @Autowired
    private org.springframework.boot.jpa.test.autoconfigure.TestEntityManager entityManager;

    @Test
    void should_return_true_when_email_exists() {
        var id = new com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId(UUID.randomUUID().toString());
        var email = new com.github.chunpingwang.ollamaagentoutcome.domain.Email("exists@test.com");
        var displayName = new com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName("Existing User");
        
        saveProfilePort.save(new com.github.chunpingwang.ollamaagentoutcome.domain.Profile(id, email, displayName));
        entityManager.flush();
        entityManager.clear();

        assertThat(existsByEmailPort.existsByEmail(email.address())).isTrue();
    }

    @Test
    void should_return_false_when_email_does_not_exist() {
        assertThat(existsByEmailPort.existsByEmail("nonexistent@example.com")).isFalse();
    }
}

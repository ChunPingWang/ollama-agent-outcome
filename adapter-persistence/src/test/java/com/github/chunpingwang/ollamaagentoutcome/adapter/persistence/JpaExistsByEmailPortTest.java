package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.ExistsByEmailPort;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JpaExistsByEmailPort.class)
@Transactional
class JpaExistsByEmailPortTest {

    @Autowired
    private ExistsByEmailPort existsByEmailPort;

    @Autowired
    private ProfileJpaRepository repository;

    @Test
    void should_return_true_when_email_exists() {
        // Given
        String id = UUID.randomUUID().toString();
        ProfileEntity entity = new ProfileEntity();
        entity.setId(id);
        entity.setEmail("exists@example.com");
        entity.setName("Exists User");
        entity.setAge(30);
        entity.setNew(true);
        repository.save(entity);

        // When
        boolean exists = existsByEmailPort.existsByEmail("exists@example.com");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void should_return_false_when_email_does_not_exist() {
        // When
        boolean exists = existsByEmailPort.existsByEmail("nonexistent@example.com");

        // Then
        assertThat(exists).isFalse();
    }
}

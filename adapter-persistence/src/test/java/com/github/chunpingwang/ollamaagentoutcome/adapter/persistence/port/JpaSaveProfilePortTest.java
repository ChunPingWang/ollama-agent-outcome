package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.port;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

import com.github.chunpingwang.ollamaagentoutcome.domain.entity.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.exception.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Bio;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.TestcontainersConfiguration;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({ JpaSaveProfilePort.class, TestcontainersConfiguration.class })
class JpaSaveProfilePortTest {

    @Autowired
    private JpaSaveProfilePort saveProfilePort;

    @Autowired
    private EntityManager entityManager;

    @Test
    void should_save_profile_successfully() {
        var id = UUID.randomUUID();
        var emailStr = "unique@example.com";
        var profile = new Profile(
                new ProfileId(id), 
                new Email(emailStr), 
                new DisplayName("User"), 
                new Bio("Hello")
        );

        saveProfilePort.save(profile);
        
        // Flush and clear to ensure data is persisted and cache cleared for next query if needed
        entityManager.flush();
        entityManager.clear();

        assertThat(entityManager.find(com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.entity.ProfileEntity.class, id))
                .isNotNull()
                .extracting(e -> e.getEmail())
                .isEqualTo(emailStr);
    }

    @Test
    void should_throw_duplicate_email_exception_when_email_exists() {
        var id1 = UUID.randomUUID();
        var emailStr = "duplicate@example.com";
        
        var profile1 = new Profile(
                new ProfileId(id1), 
                new Email(emailStr), 
                null, 
                new Bio("")
        );
        
        saveProfilePort.save(profile1);
        entityManager.flush();
        entityManager.clear();

        var id2 = UUID.randomUUID();
        var profile2 = new Profile(
                new ProfileId(id2), 
                new Email(emailStr), 
                new DisplayName("Other User"), 
                new Bio("")
        );

        assertThatThrownBy(() -> saveProfilePort.save(profile2))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("Duplicate email");
    }
}

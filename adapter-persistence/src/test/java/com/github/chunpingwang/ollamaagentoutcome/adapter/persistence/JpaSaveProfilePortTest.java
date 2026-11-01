package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;



import org.springframework.test.annotation.DirtiesContext;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
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
@Import({ JpaSaveProfilePort.class, ProfileEntityConverter.class })
class JpaSaveProfilePortTest {

    @Autowired private JpaSaveProfilePort saveProfilePort;
    @Autowired private org.springframework.boot.jpa.test.autoconfigure.TestEntityManager entityManager;

    @Test
    void should_save_and_load_profile() {
        UUID id = UUID.randomUUID();
        Email email = new Email("test@example.com");
        DisplayName displayName = new DisplayName("Tester");
        var profile = new Profile(new ProfileId(id), email, displayName);

        var saved = saveProfilePort.save(profile);
        entityManager.flush();
        entityManager.clear();

        assertThat(saved.id()).isEqualTo(new ProfileId(id));
        assertThat(saved.email()).isEqualTo(email);
        assertThat(saved.displayName()).isEqualTo(displayName);
        
        // Verify it can be loaded back via the entity manager to ensure persistence worked correctly in DB
        ProfileEntity foundEntity = entityManager.find(ProfileEntity.class, id);
        assertThat(foundEntity).isNotNull();
        assertThat(foundEntity.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void should_throw_duplicate_exception_on_duplicate_email() {
        UUID id1 = UUID.randomUUID();
        Email email = new Email("unique@example.com");
        DisplayName dn = new DisplayName("User 1");
        var p1 = new Profile(new ProfileId(id1), email, dn);
        
        saveProfilePort.save(p1);
        entityManager.flush();
        entityManager.clear();

        UUID id2 = UUID.randomUUID();
        var p2 = new Profile(new ProfileId(id2), email, new DisplayName("User 2"));

        // This must be the last assertion line because transaction is aborted after constraint violation
        assertThatThrownBy(() -> saveProfilePort.save(p2))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("Email already exists");
    }
}

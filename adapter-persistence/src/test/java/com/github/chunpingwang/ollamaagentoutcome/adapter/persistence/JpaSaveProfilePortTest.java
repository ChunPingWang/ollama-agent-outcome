package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;


import org.springframework.test.annotation.DirtiesContext;
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

    @Autowired
    private JpaSaveProfilePort saveProfilePort;

    @Autowired
    private org.springframework.boot.jpa.test.autoconfigure.TestEntityManager entityManager;

    @Test
    void should_save_profile_and_return_with_id() {
        var id = new ProfileId(UUID.randomUUID());
        var email = new Email("test@example.com");
        var displayName = new DisplayName("John Doe");
        var profile = new Profile(id, email, displayName);

        var saved = saveProfilePort.save(profile);

        assertThat(saved).isNotNull();
        assertThat(saved.id()).isEqualTo(id);
        assertThat(saved.email()).isEqualTo(email);
        assertThat(saved.displayName()).isEqualTo(displayName);

        // Verify persistence in DB
        entityManager.flush();
        entityManager.clear();

        var found = entityManager.find(ProfileEntity.class, id.value());
        assertThat(found).isNotNull();
        assertThat(found.getEmail()).isEqualTo("test@example.com");
        assertThat(found.getDisplayName()).isEqualTo("John Doe");
    }

    @Test
    void should_throw_duplicate_exception_when_email_already_exists() {
        var id1 = new ProfileId(UUID.randomUUID());
        var email = new Email("unique@example.com");
        var displayName = new DisplayName("User One");
        var profile1 = new Profile(id1, email, displayName);

        saveProfilePort.save(profile1);
        entityManager.flush();
        entityManager.clear();

        var id2 = new ProfileId(UUID.randomUUID());
        var profile2 = new Profile(id2, email, new DisplayName("User Two"));

        // This must be the last assertion because transaction is aborted after constraint violation
        assertThatThrownBy(() -> saveProfilePort.save(profile2))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("Duplicate email");
    }
}

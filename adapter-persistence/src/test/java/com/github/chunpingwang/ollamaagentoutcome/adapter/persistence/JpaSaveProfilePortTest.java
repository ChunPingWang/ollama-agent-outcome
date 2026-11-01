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
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;

// 測試使用 TestEntityManager（需要交易）：保留測試交易，以 flush()/clear() 控制一級快取；每個測試後重建 context
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JpaSaveProfilePort.class, ProfileEntityConverter.class})
class JpaSaveProfilePortTest {

    @Autowired
    private SaveProfilePort saveProfilePort;

    @Autowired
    private org.springframework.boot.jpa.test.autoconfigure.TestEntityManager entityManager;

    @Test
    void should_save_new_profile() {
        // Given
        UUID id = UUID.randomUUID();
        String emailStr = "new@example.com";
        String displayName = "New User";
        
        Profile profile = Profile.create(id, emailStr, displayName);

        // When
        Profile saved = saveProfilePort.save(profile);
        entityManager.flush();
        entityManager.clear();

        // Then
        assertThat(saved).isNotNull();
        assertThat(saved.getId().value()).isEqualTo(id);
        assertThat(saved.getEmailString()).isEqualTo(emailStr.toLowerCase());
        assertThat(saved.getDisplayNameString()).isEqualTo(displayName);
        
        // Verify in DB directly to ensure persistence state is clean
        ProfileEntity entity = entityManager.find(ProfileEntity.class, id);
        assertThat(entity).isNotNull();
        assertThat(entity.getEmail()).isEqualTo(emailStr.toLowerCase());
    }

    @Test
    void should_throw_duplicate_email_exception_when_email_exists() {
        // Given: Use a constant string for the duplicate check to trigger constraint violation reliably.
        String sharedEmail = "dup@example.com";
        UUID firstId = UUID.randomUUID();
        UUID secondId = UUID.randomUUID();

        Profile firstProfile = Profile.create(firstId, sharedEmail, "First");
        
        // Save first one (this must succeed)
        saveProfilePort.save(firstProfile);
        entityManager.flush();
        entityManager.clear();

        // When & Then: The second save with the SAME email string must fail due to unique constraint.
        // This assertion MUST be the last line of this test method because PostgreSQL aborts transaction on constraint violation.
        Profile secondProfile = Profile.create(secondId, sharedEmail, "Second");
        assertThatThrownBy(() -> saveProfilePort.save(secondProfile))
            .isInstanceOf(DuplicateEmailException.class)
            .hasMessageContaining("email already exists");
            
        // Note: We cannot verify DB state or flush after this point in the same transaction/test method easily without restarting context.
        // The exception translation proves the port handles it correctly.
    }
}

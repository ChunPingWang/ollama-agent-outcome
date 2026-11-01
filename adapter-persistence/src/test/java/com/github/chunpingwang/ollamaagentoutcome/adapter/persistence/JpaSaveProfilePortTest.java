package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;




import org.springframework.test.annotation.DirtiesContext;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.ExistsByEmailPort;

// 測試使用 TestEntityManager（需要交易）：保留測試交易，以 flush()/clear() 控制一級快取；每個測試後重建 context
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({ JpaSaveProfilePort.class, ProfileEntityConverter.class })
class JpaSaveProfilePortTest {

    @Autowired
    private SaveProfilePort saveProfilePort;

    @Autowired
    private ExistsByEmailPort existsByEmailPort;

    @Autowired
    private org.springframework.boot.jpa.test.autoconfigure.TestEntityManager entityManager;

    @Test
    void should_save_profile_successfully() {
        String id = UUID.randomUUID().toString();
        Email email = new Email("test@example.com");
        DisplayName displayName = new DisplayName("Test User");
        
        Profile profile = Profile.create(new ProfileId(id), email, displayName);

        // Verify no exception is thrown during save
        saveProfilePort.save(profile);
        
        entityManager.flush();
        entityManager.clear();
        
        assertThat(entityManager.find(ProfileEntity.class, id)).isNotNull();
        assertThat(existsByEmailPort.existsByEmail(email.address(), null)).isTrue();
    }

    @Test
    void should_throw_duplicate_email_exception_when_saving_same_email_twice() {
        String id1 = UUID.randomUUID().toString();
        String id2 = UUID.randomUUID().toString();
        Email email = new Email("duplicate@example.com");
        DisplayName displayName = new DisplayName("User 1");
        
        Profile firstProfile = Profile.create(new ProfileId(id1), email, displayName);
        saveProfilePort.save(firstProfile);
        entityManager.flush();
        entityManager.clear();

        Profile secondProfile = Profile.create(new ProfileId(id2), email, new DisplayName("User 2"));

        // This must be the last line of this method due to transaction abortion on constraint violation
        assertThatThrownBy(() -> saveProfilePort.save(secondProfile))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("Duplicate email found");
                
        // Note: We cannot verify DB state here because the transaction is aborted.
        // The test passes if the exception is thrown as expected.
    }
    
    @Test
    void should_check_exists_by_email_correctly() {
        String id = UUID.randomUUID().toString();
        Email email = new Email("check@example.com");
        Profile profile = Profile.create(new ProfileId(id), email, new DisplayName("Check User"));
        
        saveProfilePort.save(profile);
        entityManager.flush();
        entityManager.clear();
        
        assertThat(existsByEmailPort.existsByEmail(email.address(), null)).isTrue();
        assertThat(existsByEmailPort.existsByEmail("nonexistent@example.com", null)).isFalse();
    }
}

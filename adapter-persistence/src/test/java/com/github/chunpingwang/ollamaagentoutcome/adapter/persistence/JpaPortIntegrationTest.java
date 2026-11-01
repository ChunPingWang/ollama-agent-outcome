package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;



import org.springframework.test.annotation.DirtiesContext;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

// 測試使用 TestEntityManager（需要交易）：保留測試交易，以 flush()/clear() 控制一級快取；每個測試後重建 context
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({ JpaSaveProfilePort.class, JpaLoadProfilePort.class, JpaDeleteProfilePort.class })
class JpaPortIntegrationTest {

    @Autowired private JpaSaveProfilePort saveProfilePort;
    @Autowired private JpaLoadProfilePort loadProfilePort;
    @Autowired private JpaDeleteProfilePort deleteProfilePort;
    @Autowired private org.springframework.boot.jpa.test.autoconfigure.TestEntityManager entityManager;

    @Test
    void should_save_and_load_profile() {
        ProfileId id = new ProfileId(UUID.randomUUID().toString());
        Email email = new Email("test@example.com");
        DisplayName displayName = new DisplayName("Test User");
        
        Profile profile = Profile.create(id, email, displayName);
        
        saveProfilePort.save(profile);
        entityManager.flush();
        entityManager.clear();

        assertThat(loadProfilePort.findById(id.getValue()))
            .isPresent()
            .hasValueSatisfying(p -> {
                assertThat(p.getId()).isEqualTo(id);
                assertThat(p.getEmail()).isEqualTo(email);
                assertThat(p.getDisplayName()).isEqualTo(displayName.getName());
            });
    }

    @Test
    void should_throw_duplicate_email_exception_on_violation() {
        String uuid1 = UUID.randomUUID().toString();
        String uuid2 = UUID.randomUUID().toString();
        Email sameEmail = new Email("duplicate@test.com");
        
        Profile first = Profile.create(new ProfileId(uuid1), sameEmail, new DisplayName("First"));
        Profile second = Profile.create(new ProfileId(uuid2), sameEmail, new DisplayName("Second"));
        
        saveProfilePort.save(first);
        entityManager.flush(); // First insert succeeds
        
        // Second insert with duplicate email must be the last assertion in this method
        assertThatThrownBy(() -> saveProfilePort.save(second))
            .isInstanceOf(DuplicateEmailException.class)
            .hasMessageContaining("Duplicate email address: " + sameEmail.getAddress());
            
        // Note: Transaction is aborted after DataIntegrityViolationException. 
        // Do not perform any further assertions or flushes here.
    }

    @Test
    void should_delete_profile_by_id() {
        ProfileId id = new ProfileId(UUID.randomUUID().toString());
        Email email = new Email("delete@example.com");
        DisplayName displayName = new DisplayName("To Delete");
        
        Profile profile = Profile.create(id, email, displayName);
        
        saveProfilePort.save(profile);
        entityManager.flush();
        entityManager.clear();

        deleteProfilePort.deleteById(id.getValue());
        entityManager.flush();
        entityManager.clear();

        assertThat(loadProfilePort.findById(id.getValue())).isEmpty();
    }
}

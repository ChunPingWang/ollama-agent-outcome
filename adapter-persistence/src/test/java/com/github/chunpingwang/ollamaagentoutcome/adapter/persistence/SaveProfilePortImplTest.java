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
@Import(SaveProfilePortImpl.class)
class SaveProfilePortImplTest {

    @Autowired
    private SaveProfilePortImpl saveProfilePort;

    @Autowired(required = false)
    private org.springframework.boot.jpa.test.autoconfigure.TestEntityManager em;

    @Autowired(required = false)
    private ProfileJpaRepository repository; 

    @Test
    void should_save_profile_and_mark_as_not_new() {
        UUID id = UUID.randomUUID();
        String email = "unique@example.com";
        String displayName = "New User";

        Profile profile = new Profile(
            new ProfileId(id),
            new Email(email),
            new DisplayName(displayName)
        );

        saveProfilePort.save(profile);
        
        // Flush and clear to ensure transaction state is synced with DB for next query in same test
        if (em != null) {
            em.flush();
            em.clear();
        } else if (repository != null) {
             repository.flush(); 
        }
        
        assertThat(repository.findById(id)).isPresent();
    }
    
    @Test
    void should_throw_duplicate_email_exception_on_violation() {
        UUID id1 = UUID.randomUUID();
        String email = "dup@example.com";
        Profile profile1 = new Profile(
            new ProfileId(id1),
            new Email(email),
            new DisplayName("User 1")
        );
        
        saveProfilePort.save(profile1);
        
        // Verify first one exists
        if (em != null) {
            em.flush();
            em.clear();
        } else if (repository != null) {
             repository.flush(); 
        }
        
        assertThat(repository.existsById(id1)).isTrue();

        UUID id2 = UUID.randomUUID();
        Profile profile2 = new Profile(
            new ProfileId(id2),
            new Email(email), // Same email
            new DisplayName("User 2")
        );

        // This must be the last assertion in this test method due to transaction abortion on constraint violation
        assertThatThrownBy(() -> saveProfilePort.save(profile2))
                .isInstanceOf(DuplicateEmailException.class);
    }
}

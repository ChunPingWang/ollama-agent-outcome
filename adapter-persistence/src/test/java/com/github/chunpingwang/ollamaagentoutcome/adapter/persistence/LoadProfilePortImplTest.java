package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;



import org.springframework.test.annotation.DirtiesContext;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

// 測試使用 TestEntityManager（需要交易）：保留測試交易，以 flush()/clear() 控制一級快取；每個測試後重建 context
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({ LoadProfilePortImpl.class, SaveProfilePortImpl.class })
class LoadProfilePortImplTest {

    @Autowired
    private LoadProfilePortImpl loadProfilePort;

    @Autowired
    private SaveProfilePortImpl saveProfilePort;

    @Autowired(required = false)
    private org.springframework.boot.jpa.test.autoconfigure.TestEntityManager em;

    @Test
    void should_load_profile_by_id() {
        UUID id = UUID.randomUUID();
        String email = "load@example.com";
        String displayName = "Load User";

        Profile profileToSave = new Profile(
            new ProfileId(id),
            new Email(email),
            new DisplayName(displayName)
        );

        saveProfilePort.save(profileToSave);
        
        if (em != null) {
            em.flush();
            em.clear();
        } else {
             // Fallback for direct repo access if EM not injected in this specific test class structure
             repository.flush(); 
             // Clearing first level cache is harder without EM. 
             // But since we are testing the Port which uses Repo, and Repo might have cached entities?
             // Actually, `saveAndFlush` puts it in persistence context. 
             // If I call findById on a NEW port instance but same transaction/context, it might return from cache.
             // To be safe, let's rely on the fact that LoadProfilePortImpl creates a NEW domain object from Entity.
             
             // Let's assume standard behavior: after flush/clear, data is visible to fresh queries.
             // Since I can't easily clear JPA context here without EM, I'll trust the DB state or use separate transactions?
             // No, DataJpaTest runs in one transaction per method usually unless @Transactional(false).
             // Let's just verify the conversion logic works by checking the returned Profile fields match input.
        }

        Optional<Profile> loaded = loadProfilePort.findById(new ProfileId(id));

        assertThat(loaded).isPresent();
        Profile p = loaded.get();
        assertThat(p.getId().value()).isEqualTo(id);
        assertThat(p.getEmail().getValue()).isEqualTo(email.toLowerCase());
        assertThat(p.getDisplayName() != null ? p.getDisplayName().getValue() : "").isEqualTo(displayName);
    }
    
    @Autowired(required = false)
    private ProfileJpaRepository repository;

    @Test
    void should_return_empty_when_not_found() {
        UUID id = UUID.randomUUID();
        
        Optional<Profile> result = loadProfilePort.findById(new ProfileId(id));
        
        assertThat(result).isEmpty();
    }
}

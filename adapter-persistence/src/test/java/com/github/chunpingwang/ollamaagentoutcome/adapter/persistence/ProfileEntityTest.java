package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;




import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.annotation.DirtiesContext;
import static org.assertj.core.api.Assertions.*;import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

/**
 * 驗證 ProfileEntity 的 JPA 映射與基本行為。
 */
// 每次 repository 呼叫各自交易：避免一級快取與交易中止問題；每個測試後重建 context 與 schema
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProfileEntityTest {

    @Autowired
    private ProfileRepository profileRepository;

    @Test
    void should_save_and_load_profile_with_generated_uuid() {
        // Given
        String email = "test@example.com";
        String displayName = "Test User";

        // When
        ProfileEntity entity = new ProfileEntity();
        entity.setEmail(email);
        entity.setDisplayName(displayName);
        
        ProfileEntity saved = profileRepository.save(entity);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getEmail()).isEqualTo(email);
        assertThat(saved.getDisplayName()).isEqualTo(displayName);
        
        // Verify persistence via flush and clear to ensure it's actually in DB
        profileRepository.flush();
        UUID loadedId = saved.getId();
        var found = profileRepository.findById(loadedId);
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo(email);
    }
    
    /**
     * 驗證唯一性約束：重複 Email 應拋出異常。
     */
    @Test
    void should_throw_on_duplicate_email() {
        // Given
        String email = "unique@example.com";
        
        ProfileEntity first = new ProfileEntity();
        first.setEmail(email);
        first.setDisplayName("First");
        profileRepository.saveAndFlush(first); 

        // When & Then
        ProfileEntity second = new ProfileEntity();
        second.setEmail(email);
        second.setDisplayName("Second");
        
        // PostgreSQL throws DataIntegrityViolationException on unique constraint violation
        assertThatThrownBy(() -> profileRepository.saveAndFlush(second))
            .isInstanceOf(DataIntegrityViolationException.class)
            .hasMessageContaining("duplicate key value violates unique constraint")
            .hasMessageContaining("profiles_email_key");
    }
}

package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;



import org.springframework.test.annotation.DirtiesContext;
import static org.assertj.core.api.Assertions.*;
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
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

// 測試使用 TestEntityManager（需要交易）：保留測試交易，以 flush()/clear() 控制一級快取；每個測試後重建 context
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JpaSaveProfilePort.class)
class JpaSaveProfilePortTest {

    @Autowired
    private JpaSaveProfilePort saveProfilePort;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void should_save_new_profile() {
        String id = UUID.randomUUID().toString();
        var profile = createValidProfile(id);
        
        saveProfilePort.save(profile);
        entityManager.flush();
        entityManager.clear();
        
        assertThat(entityManager.find(ProfileEntity.class, id)).isNotNull();
    }

    @Test
    void should_update_existing_profile() {
        String id = UUID.randomUUID().toString();
        var initialProfile = createValidProfile(id);
        
        saveProfilePort.save(initialProfile);
        entityManager.flush();
        entityManager.clear();
        
        // 模擬更新：建立一個具有相同 ID 但不同資料的 Profile（在實際應用中，UpdateService 會先 Load 再 Update Domain Object）
        // 這裡為了測試 Port 的 save 行為是否支援更新，我們直接呼叫 save。
        // 注意：JPA Entity 實作 Persistable，isNew=false 時 save 通常視為 update。
        var updatedEmail = new Email("updated@example.com");
        var updatedDisplayName = new DisplayName("Updated Name");
        var updatedProfile = Profile.create(
            new ProfileId(id), 
            updatedEmail, 
            updatedDisplayName
        );
        
        saveProfilePort.save(updatedProfile);
        entityManager.flush();
        entityManager.clear();
        
        var foundEntity = entityManager.find(ProfileEntity.class, id);
        assertThat(foundEntity.getEmail()).isEqualTo("updated@example.com");
        assertThat(foundEntity.getDisplayName()).isEqualTo("Updated Name");
    }

    @Test
    void should_throw_duplicate_email_exception_on_conflict() {
        String id1 = UUID.randomUUID().toString();
        String id2 = UUID.randomUUID().toString();
        var profile1 = createValidProfile(id1);
        
        saveProfilePort.save(profile1);
        entityManager.flush();
        entityManager.clear();
        
        var profile2 = createValidProfileWithSameEmail(id2, "dup@test.com");
        
        // DataIntegrityViolationException 必須是該測試方法的最後一行斷言
        assertThatThrownBy(() -> saveProfilePort.save(profile2))
            .isInstanceOf(DuplicateEmailException.class)
            .hasMessageContaining("email already exists");
    }

    private Profile createValidProfile(String id) {
        return Profile.create(new ProfileId(id), new Email("test@example.com"), new DisplayName("Tester"));
    }

    private Profile createValidProfileWithSameEmail(String id, String emailAddr) {
        return Profile.create(new ProfileId(id), new Email(emailAddr), new DisplayName("Dup User"));
    }
}

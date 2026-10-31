package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;



import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

// 每次 repository 呼叫各自交易：避免一級快取與交易中止問題；每個測試後重建 context 與 schema
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({ JpaSaveProfilePort.class, ProfileEntityConverter.class })
class JpaSaveProfilePortTest {

    @Autowired
    private JpaSaveProfilePort saveProfilePort;

    @Autowired
    private org.springframework.boot.jpa.test.autoconfigure.TestEntityManager entityManager;

    @Test
    void should_save_profile_successfully() {
        var id = new ProfileId(UUID.randomUUID().toString());
        var email = new Email("test@example.com");
        var displayName = new DisplayName("John Doe");
        var profile = new Profile(id, email, displayName);

        var saved = saveProfilePort.save(profile);

        assertThat(saved).isNotNull();
        assertThat(saved.id()).isEqualTo(id);
        
        // 清除快取以驗證資料庫狀態
        entityManager.flush();
        entityManager.clear();

        var found = entityManager.find(ProfileEntity.class, saved.id().value());
        assertThat(found).isNotNull();
        assertThat(found.getEmail()).isEqualTo(email.address());
        assertThat(found.getDisplayName()).isEqualTo(displayName.name());
    }

    @Test
    void should_throw_duplicate_email_exception_on_violation() {
        var id1 = new ProfileId(UUID.randomUUID().toString());
        var email = new Email("unique@test.com");
        var displayName = new DisplayName("User One");
        var profile1 = new Profile(id1, email, displayName);

        saveProfilePort.save(profile1);
        entityManager.flush();
        entityManager.clear();

        var id2 = new ProfileId(UUID.randomUUID().toString());
        var profile2 = new Profile(id2, email, new DisplayName("User Two"));

        assertThatThrownBy(() -> saveProfilePort.save(profile2))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("電子郵件已存在");
    }
}

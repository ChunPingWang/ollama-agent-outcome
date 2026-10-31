package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;

/**
 * 測試 JpaSaveProfilePort 是否正確呼叫 Repository 並處理轉換。
 * 容器設定已由 PersistenceTestApplication 掛載。
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JpaSaveProfilePort.class, ProfileConverter.class})
class JpaSaveProfilePortTest {

    @Autowired
    private SaveProfilePort saveProfilePort;

    @Autowired
    private ProfileRepository profileRepository;

    @Test
    void should_persist_and_load_profile() {
        // Given
        UUID id = UUID.randomUUID();
        Profile profile = new Profile(new ProfileId(id), "張三", new Email("zhangsan@example.com"));

        // When
        Profile saved = saveProfilePort.save(profile);

        // Then
        assertThat(saved).isNotNull();
        assertThat(saved.id().value()).isEqualTo(id);
        
        // Verify in database
        var entity = profileRepository.findById(id);
        assertThat(entity).isPresent();
        assertThat(entity.get().getName()).isEqualTo("張三");
        assertThat(entity.get().getEmail()).isEqualTo("zhangsan@example.com");
    }
}

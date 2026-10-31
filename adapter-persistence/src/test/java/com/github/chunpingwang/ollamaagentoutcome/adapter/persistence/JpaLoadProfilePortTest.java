package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.UUID;

import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

/**
 * 測試 LoadProfilePort 查詢邏輯。
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JpaLoadProfilePort.class, JpaSaveProfilePort.class, ProfileConverter.class})
class JpaLoadProfilePortTest {

    @Autowired
    private LoadProfilePort loadProfilePort;

    @Autowired
    private ProfileRepository repository;

    @Test
    void should_load_profile_by_id() {
        // Given
        UUID id = UUID.randomUUID();
        ProfileId profileId = new ProfileId(id);
        Email email = new Email("user@example.com");
        Profile profile = new Profile(profileId, "John Doe", email);
        
        // 使用 Port 儲存，確保 isNew 狀態正確
        JpaSaveProfilePort savePort = new JpaSaveProfilePort(repository, new ProfileConverter());
        savePort.save(profile);

        // When
        Optional<Profile> loaded = loadProfilePort.findById(profileId);

        // Then
        assertThat(loaded).isPresent();
        assertThat(loaded.get().getId()).isEqualTo(profileId);
        assertThat(loaded.get().getName()).isEqualTo("John Doe");
        assertThat(loaded.get().getEmail()).isEqualTo(email);
    }

    @Test
    void should_return_empty_when_profile_not_found() {
        // Given
        UUID id = UUID.randomUUID();
        ProfileId profileId = new ProfileId(id);

        // When
        Optional<Profile> loaded = loadProfilePort.findById(profileId);

        // Then
        assertThat(loaded).isEmpty();
    }
}

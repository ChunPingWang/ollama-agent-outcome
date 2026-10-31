package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.exception.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

/**
 * 測試 SaveProfilePort 實作，包含重複 Email 檢查與儲存邏輯。
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JpaSaveProfilePort.class, ProfileConverter.class})
class JpaSaveProfilePortTest {

    @Autowired
    private SaveProfilePort saveProfilePort;

    @Autowired
    private ProfileRepository repository;

    @Test
    void should_save_profile_successfully() {
        // Given
        UUID id = UUID.randomUUID();
        ProfileId profileId = new ProfileId(id);
        Email email = new Email("new@example.com");
        Profile profile = new Profile(profileId, "John Doe", email);

        // When
        Profile saved = saveProfilePort.save(profile);

        // Then
        assertThat(saved.getId()).isEqualTo(profileId);
        assertThat(saved.getName()).isEqualTo("John Doe");
        assertThat(saved.getEmail()).isEqualTo(email);
        
        // Verify in DB
        assertThat(repository.findById(id)).isPresent();
    }

    @Test
    void should_throw_duplicate_email_exception_when_email_exists() {
        // Given
        UUID id1 = UUID.randomUUID();
        ProfileId profileId1 = new ProfileId(id1);
        Email email = new Email("duplicate@example.com");
        Profile profile1 = new Profile(profileId1, "John Doe", email);
        saveProfilePort.save(profile1);

        UUID id2 = UUID.randomUUID();
        ProfileId profileId2 = new ProfileId(id2);
        Profile profile2 = new Profile(profileId2, "Jane Doe", email);

        // When & Then
        assertThatThrownBy(() -> saveProfilePort.save(profile2))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("Email already exists");
    }
}

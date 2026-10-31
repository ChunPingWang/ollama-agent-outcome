package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

/**
 * 測試 DeleteProfilePort 刪除邏輯。
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JpaDeleteProfilePort.class, JpaSaveProfilePort.class, ProfileConverter.class})
class JpaDeleteProfilePortTest {

    @Autowired
    private DeleteProfilePort deleteProfilePort;

    @Autowired
    private ProfileRepository repository;

    @Test
    void should_delete_profile_by_id() {
        // Given
        UUID id = UUID.randomUUID();
        ProfileId profileId = new ProfileId(id);
        Email email = new Email("user@example.com");
        Profile profile = new Profile(profileId, "John Doe", email);
        
        // 使用 Port 儲存，確保 isNew 狀態正確
        JpaSaveProfilePort savePort = new JpaSaveProfilePort(repository, new ProfileConverter());
        savePort.save(profile);

        // When
        deleteProfilePort.deleteById(profileId);

        // Then
        assertThat(repository.findById(id)).isEmpty();
    }

    @Test
    void should_not_throw_when_deleting_non_existent_profile() {
        // Given
        UUID id = UUID.randomUUID();
        ProfileId profileId = new ProfileId(id);

        // When & Then
        // Should not throw exception
        deleteProfilePort.deleteById(profileId);
        
        // Verify it's still empty
        assertThat(repository.findById(id)).isEmpty();
    }
}

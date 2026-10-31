package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 測試 JpaFindProfileByIdPort 實作，驗證依 ID 查詢邏輯。
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JpaFindProfileByIdPort.class, ProfileEntityConverter.class})
@Transactional
@DirtiesContext
class JpaFindProfileByIdPortTest {

    @Autowired
    private JpaFindProfileByIdPort findProfileByIdPort;

    @Autowired
    private ProfileRepository profileRepository;

    @Test
    void should_find_profile_by_id() {
        // Given
        UUID id = UUID.randomUUID();
        ProfileEntity entity = new ProfileEntity();
        entity.setId(id);
        entity.setEmail("user@example.com");
        entity.setName("John Doe");
        entity.setAge(25);
        entity.setNew(false);
        profileRepository.save(entity);
        profileRepository.flush();

        ProfileId profileId = new ProfileId(id);

        // When
        Optional<Profile> found = findProfileByIdPort.findById(profileId);

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(profileId);
        assertThat(found.get().getEmail().getValue()).isEqualTo("user@example.com");
    }

    @Test
    void should_return_empty_when_profile_not_found() {
        // Given
        UUID id = UUID.randomUUID();
        ProfileId profileId = new ProfileId(id);

        // When
        Optional<Profile> found = findProfileByIdPort.findById(profileId);

        // Then
        assertThat(found).isEmpty();
    }
}

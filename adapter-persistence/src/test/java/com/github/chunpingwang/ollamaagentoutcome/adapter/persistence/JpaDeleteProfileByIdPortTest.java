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
 * 測試 JpaDeleteProfileByIdPort 實作，驗證刪除邏輯。
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JpaDeleteProfileByIdPort.class, JpaFindProfileByIdPort.class, ProfileEntityConverter.class})
@Transactional
@DirtiesContext
class JpaDeleteProfileByIdPortTest {

    @Autowired
    private JpaDeleteProfileByIdPort deleteProfileByIdPort;

    @Autowired
    private JpaFindProfileByIdPort findProfileByIdPort;

    @Autowired
    private ProfileRepository profileRepository;

    @Test
    void should_delete_profile_by_id() {
        // Given
        UUID id = UUID.randomUUID();
        ProfileEntity entity = new ProfileEntity();
        entity.setId(id);
        entity.setEmail("delete@example.com");
        entity.setName("Delete Me");
        entity.setAge(20);
        entity.setNew(false);
        profileRepository.save(entity);
        profileRepository.flush();

        ProfileId profileId = new ProfileId(id);

        // When
        deleteProfileByIdPort.deleteById(profileId);
        profileRepository.flush();

        // Then
        Optional<Profile> found = findProfileByIdPort.findById(profileId);
        assertThat(found).isEmpty();
    }
}

package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import com.github.chunpingwang.ollamaagentoutcome.domain.Bio;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 測試 JPA Load Profile Port 的查詢邏輯。
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JpaLoadProfilePort.class, ProfileConverter.class})
class JpaLoadProfilePortTest {

    @Autowired
    private JpaLoadProfilePort loadProfilePort;

    @Autowired
    private ProfileRepository profileRepository;

    private Profile existingProfile;

    @BeforeEach
    void setUp() {
        existingProfile = new Profile(
            new ProfileId("load-id"),
            new Email("load@test.com"),
            new Name("Load User"),
            new Bio("Load Bio")
        );
        ProfileEntity entity = ProfileConverter.toEntity(existingProfile);
        profileRepository.saveAndFlush(entity);
    }

    @Test
    @DisplayName("應能根據 ID 載入個人資料")
    void should_load_profile_by_id() {
        // When
        Optional<Profile> loadedProfile = loadProfilePort.findById("load-id");

        // Then
        assertThat(loadedProfile).isPresent();
        assertThat(loadedProfile.get().id().value()).isEqualTo("load-id");
        assertThat(loadedProfile.get().email().value()).isEqualTo("load@test.com");
        assertThat(loadedProfile.get().name().value()).isEqualTo("Load User");
        assertThat(loadedProfile.get().bio().value()).isEqualTo("Load Bio");
    }

    @Test
    @DisplayName("當 ID 不存在時，應回傳空 Optional")
    void should_return_empty_when_id_not_found() {
        // When
        Optional<Profile> loadedProfile = loadProfilePort.findById("non-existent-id");

        // Then
        assertThat(loadedProfile).isEmpty();
    }
}

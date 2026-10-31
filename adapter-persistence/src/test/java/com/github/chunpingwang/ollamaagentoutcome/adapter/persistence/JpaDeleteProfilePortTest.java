package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThat;

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
 * 測試 JPA Delete Profile Port 的刪除邏輯。
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JpaDeleteProfilePort.class, ProfileConverter.class})
class JpaDeleteProfilePortTest {

    @Autowired
    private JpaDeleteProfilePort deleteProfilePort;

    @Autowired
    private ProfileRepository profileRepository;

    private String profileId;

    @BeforeEach
    void setUp() {
        profileId = "delete-id";
        Profile profile = new Profile(
            new ProfileId(profileId),
            new Email("delete@test.com"),
            new Name("Delete User"),
            new Bio("Delete Bio")
        );
        ProfileEntity entity = ProfileConverter.toEntity(profile);
        profileRepository.saveAndFlush(entity);
    }

    @Test
    @DisplayName("應能成功刪除存在的個人資料")
    void should_delete_profile_successfully() {
        // When
        deleteProfilePort.deleteById(profileId);

        // Then
        assertThat(profileRepository.findById(profileId)).isEmpty();
    }

    @Test
    @DisplayName("刪除不存在的 ID 時不應拋出例外")
    void should_not_throw_when_deleting_non_existent() {
        // When & Then
        deleteProfilePort.deleteById("non-existent-id");
    }
}

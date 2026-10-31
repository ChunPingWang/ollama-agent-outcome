package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;

import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

/**
 * {@link JpaDeleteProfilePort} 的整合測試。
 * 驗證刪除個人資料的功能。
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JpaDeleteProfilePort.class})
class JpaDeleteProfilePortTest {

    @Autowired
    private JpaDeleteProfilePort deleteProfilePort;

    @Autowired
    private ProfileRepository profileRepository;

    @Test
    void should_delete_profile_when_exists() {
        // Arrange
        String name = "John Doe";
        String email = "john@example.com";
        ProfileEntity entity = profileRepository.save(new ProfileEntity(null, name, email));
        ProfileId id = new ProfileId(entity.getId());

        // Act
        deleteProfilePort.deleteById(id);

        // Assert
        // 由於在同一交易內，JPA 一級快取可能仍持有已刪除實體，需強制同步並清除快取
        profileRepository.flush();
        Optional<ProfileEntity> deletedEntity = profileRepository.findById(entity.getId());
        assertThat(deletedEntity).isEmpty();
    }

    @Test
    void should_throw_exception_when_profile_not_found() {
        // Arrange
        ProfileId nonExistentId = new ProfileId("non-existent-id");

        // Act & Assert
        assertThatThrownBy(() -> deleteProfilePort.deleteById(nonExistentId))
                .isInstanceOf(ProfileNotFoundException.class)
                .hasMessageContaining("Profile not found");
    }
}

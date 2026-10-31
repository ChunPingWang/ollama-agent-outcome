package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

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
 * {@link JpaLoadProfilePort} 的整合測試。
 * 驗證根據 ID 查詢個人資料的功能。
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JpaLoadProfilePort.class})
class JpaLoadProfilePortTest {

    @Autowired
    private JpaLoadProfilePort loadProfilePort;

    @Autowired
    private ProfileRepository profileRepository;

    @Test
    void should_load_profile_when_exists() {
        // Arrange
        String name = "John Doe";
        String email = "john@example.com";
        ProfileEntity entity = profileRepository.save(new ProfileEntity(null, name, email));
        String savedId = entity.getId();
        
        // Act
        Optional<Profile> profile = loadProfilePort.findById(new ProfileId(savedId));

        // Assert
        assertThat(profile).isPresent();
        Profile p = profile.get();
        assertThat(p.getId().getValue()).isEqualTo(savedId);
        assertThat(p.getName()).isEqualTo(name);
        assertThat(p.getEmail().getValue()).isEqualTo(email);
    }

    @Test
    void should_return_empty_when_profile_not_found() {
        // Arrange
        ProfileId nonExistentId = new ProfileId("non-existent-id");

        // Act
        Optional<Profile> profile = loadProfilePort.findById(nonExistentId);

        // Assert
        assertThat(profile).isEmpty();
    }
}

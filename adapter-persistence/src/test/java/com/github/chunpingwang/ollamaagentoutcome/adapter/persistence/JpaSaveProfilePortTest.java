package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 測試 JPA Save Profile Port 的儲存邏輯與唯一性約束。
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JpaSaveProfilePort.class, ProfileConverter.class})
class JpaSaveProfilePortTest {

    @Autowired
    private JpaSaveProfilePort saveProfilePort;

    @Autowired
    private ProfileRepository profileRepository;

    @Test
    @DisplayName("應能成功儲存個人資料")
    void should_save_profile_successfully() {
        // Given
        Profile profile = new Profile(
            new ProfileId("test-id-1"),
            new Email("save@test.com"),
            new Name("Save User"),
            new com.github.chunpingwang.ollamaagentoutcome.domain.Bio("Bio")
        );

        // When
        saveProfilePort.save(profile);

        // Then
        assertThat(profileRepository.findById("test-id-1")).isPresent();
    }

    @Test
    @DisplayName("當電子郵件重複時，應拋出 DuplicateEmailException")
    void should_throw_duplicate_email_exception_on_duplicate() {
        // Given
        Profile profile1 = new Profile(
            new ProfileId("dup-id-1"),
            new Email("duplicate@test.com"),
            new Name("User 1"),
            new com.github.chunpingwang.ollamaagentoutcome.domain.Bio("Bio 1")
        );
        Profile profile2 = new Profile(
            new ProfileId("dup-id-2"),
            new Email("duplicate@test.com"), // Same email
            new Name("User 2"),
            new com.github.chunpingwang.ollamaagentoutcome.domain.Bio("Bio 2")
        );

        // When & Then
        saveProfilePort.save(profile1);
        assertThatThrownBy(() -> saveProfilePort.save(profile2))
            .isInstanceOf(DuplicateEmailException.class)
            .hasMessageContaining("duplicate@test.com");
    }
}

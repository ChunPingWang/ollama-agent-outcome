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

import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 測試 JpaFindProfileByEmailPort 實作，驗證依 Email 查詢邏輯。
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JpaFindProfileByEmailPort.class, ProfileEntityConverter.class})
@Transactional
@DirtiesContext
class JpaFindProfileByEmailPortTest {

    @Autowired
    private JpaFindProfileByEmailPort findProfileByEmailPort;

    @Autowired
    private ProfileRepository profileRepository;

    @Test
    void should_find_profile_by_email() {
        // Given
        UUID id = UUID.randomUUID();
        String emailValue = "user@example.com";
        ProfileEntity entity = new ProfileEntity();
        entity.setId(id);
        entity.setEmail(emailValue);
        entity.setName("John Doe");
        entity.setAge(25);
        entity.setNew(false);
        profileRepository.save(entity);
        profileRepository.flush();

        Email email = new Email(emailValue);

        // When
        Optional<Profile> found = findProfileByEmailPort.findByEmail(email);

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo(email);
    }

    @Test
    void should_return_empty_when_email_not_found() {
        // Given
        Email email = new Email("nonexistent@example.com");

        // When
        Optional<Profile> found = findProfileByEmailPort.findByEmail(email);

        // Then
        assertThat(found).isEmpty();
    }
}

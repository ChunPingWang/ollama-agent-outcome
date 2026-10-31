package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import com.github.chunpingwang.ollamaagentoutcome.domain.Age;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 測試 JpaSaveProfilePort 實作，驗證其呼叫 Repository 並正確轉換回 Domain 物件。
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JpaSaveProfilePort.class, ProfileEntityConverter.class})
@Transactional
@DirtiesContext
class JpaSaveProfilePortTest {

    @Autowired
    private JpaSaveProfilePort saveProfilePort;

    @Test
    void should_persist_and_load_profile() {
        // Given
        UUID id = UUID.randomUUID();
        ProfileId profileId = new ProfileId(id);
        Email email = new Email("user@example.com");
        Name name = new Name("John Doe");
        Age age = new Age(25);
        Profile profile = new Profile(profileId, email, name, age);

        // When
        Profile saved = saveProfilePort.save(profile);

        // Then
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isEqualTo(profileId);
        assertThat(saved.getEmail()).isEqualTo(email);
        assertThat(saved.getName()).isEqualTo(name);
        assertThat(saved.getAge()).isEqualTo(age);
    }
}

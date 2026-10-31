package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Age;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({ JpaSaveProfilePort.class, ProfileConverter.class })
class JpaSaveProfilePortTest {

    @Autowired
    private SaveProfilePort saveProfilePort;

    @Autowired
    private ProfileJpaRepository repository;

    @Test
    void should_save_profile_successfully() {
        // Given
        String id = UUID.randomUUID().toString();
        Profile profile = new Profile(
                new ProfileId(id),
                new Email("new@example.com"),
                new Name("New User"),
                new Age(25)
        );

        // When
        Profile saved = saveProfilePort.save(profile);

        // Then
        assertThat(saved).isNotNull();
        assertThat(saved.id().getValue()).isEqualTo(id);
        
        // Verify in DB
        var entity = repository.findById(id).orElseThrow();
        assertThat(entity.getEmail()).isEqualTo("new@example.com");
    }

    @Test
    void should_throw_duplicate_email_exception_on_duplicate_email() {
        // Given
        String id1 = UUID.randomUUID().toString();
        Profile profile1 = new Profile(
                new ProfileId(id1),
                new Email("dup@example.com"),
                new Name("User 1"),
                new Age(20)
        );
        saveProfilePort.save(profile1);
        repository.flush(); // 確保寫入資料庫

        String id2 = UUID.randomUUID().toString();
        Profile profile2 = new Profile(
                new ProfileId(id2),
                new Email("dup@example.com"), // Duplicate
                new Name("User 2"),
                new Age(21)
        );

        // When & Then
        assertThatThrownBy(() -> saveProfilePort.save(profile2))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("dup@example.com");
    }
}

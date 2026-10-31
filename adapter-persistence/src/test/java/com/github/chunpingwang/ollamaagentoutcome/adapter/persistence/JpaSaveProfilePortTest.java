package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JpaSaveProfilePort.class, ProfileConverter.class})
class JpaSaveProfilePortTest {

    @Autowired
    private JpaSaveProfilePort saveProfilePort;

    @Autowired
    private ProfileRepository profileRepository;

    @Test
    void should_save_profile_successfully() {
        // given
        ProfileId id = new ProfileId(java.util.UUID.randomUUID());
        Profile profile = Profile.create(id, "John Doe", "john@example.com", "A note");

        // when
        Profile saved = saveProfilePort.save(profile);

        // then
        assertThat(saved).isNotNull();
        assertThat(saved.id()).isEqualTo(id);
        assertThat(saved.name()).isEqualTo("John Doe");
        assertThat(saved.email()).isEqualTo("john@example.com");
        assertThat(saved.note()).isEqualTo("A note");

        // Verify in DB
        assertThat(profileRepository.findById(id.id())).isPresent();
    }

    @Test
    void should_throw_duplicate_email_exception_when_email_exists() {
        // given
        ProfileId id1 = new ProfileId(java.util.UUID.randomUUID());
        Profile profile1 = Profile.create(id1, "John Doe", "unique@example.com", "Note 1");
        saveProfilePort.save(profile1);

        ProfileId id2 = new ProfileId(java.util.UUID.randomUUID());
        Profile profile2 = Profile.create(id2, "Jane Doe", "unique@example.com", "Note 2");

        // when & then
        assertThatThrownBy(() -> saveProfilePort.save(profile2))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("Email already exists");
    }
}

package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JpaLoadProfilePort.class, ProfileConverter.class})
class JpaLoadProfilePortTest {

    @Autowired
    private JpaLoadProfilePort loadProfilePort;

    @Autowired
    private ProfileRepository profileRepository;

    @Test
    void should_load_profile_by_id() {
        // given
        ProfileId id = new ProfileId(java.util.UUID.randomUUID());
        Profile profile = Profile.create(id, "John Doe", "john@example.com", "A note");
        profileRepository.saveAndFlush(ProfileConverter.toEntity(profile));

        // when
        Optional<Profile> result = loadProfilePort.findById(id);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo(id);
        assertThat(result.get().name()).isEqualTo("John Doe");
        assertThat(result.get().email()).isEqualTo("john@example.com");
        assertThat(result.get().note()).isEqualTo("A note");
    }

    @Test
    void should_return_empty_when_profile_not_found() {
        // given
        ProfileId id = new ProfileId(java.util.UUID.randomUUID());

        // when
        Optional<Profile> result = loadProfilePort.findById(id);

        // then
        assertThat(result).isEmpty();
    }
}

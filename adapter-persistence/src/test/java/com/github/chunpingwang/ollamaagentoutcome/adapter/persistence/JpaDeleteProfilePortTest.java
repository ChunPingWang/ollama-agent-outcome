package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JpaDeleteProfilePort.class, ProfileConverter.class})
class JpaDeleteProfilePortTest {

    @Autowired
    private JpaDeleteProfilePort deleteProfilePort;

    @Autowired
    private ProfileRepository profileRepository;

    @Test
    void should_delete_profile_by_id() {
        // given
        ProfileId id = new ProfileId(java.util.UUID.randomUUID());
        Profile profile = Profile.create(id, "John Doe", "john@example.com", "A note");
        profileRepository.saveAndFlush(ProfileConverter.toEntity(profile));

        // when
        deleteProfilePort.deleteById(id);

        // then
        assertThat(profileRepository.findById(id.id())).isEmpty();
    }
}

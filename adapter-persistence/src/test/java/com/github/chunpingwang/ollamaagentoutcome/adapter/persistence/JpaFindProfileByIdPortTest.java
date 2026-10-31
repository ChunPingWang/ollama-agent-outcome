package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({ JpaSaveProfilePort.class, JpaFindProfileByIdPort.class })
class JpaFindProfileByIdPortTest {

    @Autowired
    private JpaSaveProfilePort saveProfilePort;

    @Autowired
    private JpaFindProfileByIdPort findProfileByIdPort;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private org.springframework.boot.jpa.test.autoconfigure.TestEntityManager entityManager;

    @Test
    void should_find_profile_by_id() {
        // given
        Name name = new Name("Finder");
        Email email = new Email("find@example.com");
        ProfileId id = saveProfilePort.saveAndReturnId(name, email);
        
        entityManager.flush();
        entityManager.clear();

        // when
        var result = findProfileByIdPort.findById(id);

        // then
        assertThat(result).isPresent();
        Profile profile = result.get();
        assertThat(profile.id()).isEqualTo(id);
        assertThat(profile.name().text()).isEqualTo("Finder");
        assertThat(profile.email().address()).isEqualTo("find@example.com");
    }

    @Test
    void should_return_empty_when_not_found() {
        // given
        UUID nonExistentId = UUID.randomUUID();
        ProfileId id = new ProfileId(nonExistentId);

        // when
        var result = findProfileByIdPort.findById(id);

        // then
        assertThat(result).isEmpty();
    }
}

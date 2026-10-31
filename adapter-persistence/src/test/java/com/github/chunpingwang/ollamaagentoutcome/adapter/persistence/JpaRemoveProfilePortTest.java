package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({ JpaSaveProfilePort.class, JpaFindProfileByIdPort.class, JpaRemoveProfilePort.class })
class JpaRemoveProfilePortTest {

    @Autowired
    private JpaSaveProfilePort saveProfilePort;

    @Autowired
    private JpaRemoveProfilePort removeProfilePort;

    @Autowired
    private JpaFindProfileByIdPort findProfileByIdPort;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private org.springframework.boot.jpa.test.autoconfigure.TestEntityManager entityManager;

    @Test
    void should_remove_profile_by_id() {
        // given
        Name name = new Name("Removable");
        Email email = new Email("remove@example.com");
        ProfileId id = saveProfilePort.saveAndReturnId(name, email);
        
        entityManager.flush();
        entityManager.clear();

        assertThat(findProfileByIdPort.findById(id)).isPresent();

        // when
        removeProfilePort.removeById(id);
        
        // flush and clear to verify deletion in a separate context
        entityManager.flush();
        entityManager.clear();

        // then
        Optional<com.github.chunpingwang.ollamaagentoutcome.domain.Profile> result = findProfileByIdPort.findById(id);
        assertThat(result).isEmpty();
        
        // also verify count is zero
        assertThat(profileRepository.count()).isEqualTo(0L);
    }
}

package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.port;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.UUID;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import com.github.chunpingwang.ollamaagentoutcome.domain.entity.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Bio;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.TestcontainersConfiguration;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({ JpaSaveProfilePort.class, JpaFindProfileByIdPort.class, TestcontainersConfiguration.class })
class JpaFindProfileByIdPortTest {

    @Autowired
    private JpaSaveProfilePort saveProfilePort;

    @Autowired
    private JpaFindProfileByIdPort findProfileByIdPort;

    @Autowired
    private EntityManager entityManager;

    @Test
    void should_find_profile_by_id() {
        var id = UUID.randomUUID();
        var emailStr = "find@example.com";
        
        var profile = new Profile(
                new ProfileId(id), 
                new Email(emailStr), 
                new DisplayName("Finder"), 
                new Bio("Bio content")
        );
        
        saveProfilePort.save(profile);
        entityManager.flush();
        entityManager.clear();

        Optional<Profile> foundOpt = findProfileByIdPort.findById(new ProfileId(id));

        assertThat(foundOpt).isPresent();
        var found = foundOpt.get();
        assertThat(found.getId().value()).isEqualTo(id);
        assertThat(found.getEmail().address()).isEqualTo(emailStr);
        assertThat(found.getDisplayName().name()).isEqualTo("Finder");
        assertThat(found.getBio().content()).isEqualTo("Bio content");
    }

    @Test
    void should_return_empty_when_not_found() {
        var nonExistentId = UUID.randomUUID();
        
        Optional<Profile> result = findProfileByIdPort.findById(new ProfileId(nonExistentId));

        assertThat(result).isEmpty();
    }
}

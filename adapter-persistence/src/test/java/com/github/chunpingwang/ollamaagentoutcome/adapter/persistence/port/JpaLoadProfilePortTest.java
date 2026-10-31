package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.port;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.UUID;

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
import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.PersistenceTestApplication;
import jakarta.persistence.EntityManager;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({ JpaSaveProfilePort.class, JpaLoadProfilePort.class })
class JpaLoadProfilePortTest {

    @Autowired
    private JpaSaveProfilePort saveProfilePort;

    @Autowired
    private JpaLoadProfilePort loadProfilePort;

    @Autowired
    private EntityManager entityManager;

    @Test
    void should_load_profile_by_id() {
        UUID id = UUID.randomUUID();
        Profile profile = new Profile(
                new ProfileId(id),
                new Email("load@example.com"),
                new DisplayName("Loader"),
                new Bio("Loading bio"));

        saveProfilePort.save(profile);
        entityManager.flush();
        entityManager.clear();

        Optional<Profile> found = loadProfilePort.findById(new ProfileId(id));

        assertThat(found).isPresent();
        var loaded = found.get();
        assertThat(loaded.id().value()).isEqualTo(id);
        assertThat(loaded.email().address()).isEqualTo("load@example.com");
        assertThat(loaded.displayName().name()).isEqualTo("Loader");
        assertThat(loaded.bio().content()).isEqualTo("Loading bio");
    }

    @Test
    void should_return_empty_when_not_found() {
        UUID nonExistentId = UUID.randomUUID();
        
        Optional<Profile> result = loadProfilePort.findById(new ProfileId(nonExistentId));

        assertThat(result).isEmpty();
    }
}

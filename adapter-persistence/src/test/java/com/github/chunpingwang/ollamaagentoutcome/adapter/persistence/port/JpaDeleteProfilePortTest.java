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
@Import({ JpaSaveProfilePort.class, JpaLoadProfilePort.class, JpaDeleteProfilePort.class })
class JpaDeleteProfilePortTest {

    @Autowired
    private JpaSaveProfilePort saveProfilePort;

    @Autowired
    private JpaLoadProfilePort loadProfilePort;

    @Autowired
    private JpaDeleteProfilePort deleteProfilePort;

    @Autowired
    private EntityManager entityManager;

    @Test
    void should_delete_profile_by_id() {
        UUID id = UUID.randomUUID();
        Profile profile = new Profile(
                new ProfileId(id),
                new Email("delete@example.com"),
                new DisplayName("Deleter"),
                null);

        saveProfilePort.save(profile);
        entityManager.flush();
        entityManager.clear();

        deleteProfilePort.deleteById(new ProfileId(id));
        
        // Flush and clear to ensure deletion is committed and cache is cleared for subsequent query in same transaction context if needed, 
        // though typically we just verify via load or count. Here we use load to verify absence.
        entityManager.flush();
        entityManager.clear();

        Optional<Profile> result = loadProfilePort.findById(new ProfileId(id));
        assertThat(result).isEmpty();
    }
}

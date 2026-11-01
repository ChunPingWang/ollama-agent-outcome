package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;



import static org.assertj.core.api.Assertions.*;import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({ JpaSaveProfilePort.class, JpaLoadProfilePort.class })
class JpaLoadProfilePortTest {

    @Autowired
    private SaveProfilePort saveProfilePort;

    @Autowired
    private LoadProfilePort loadProfilePort;

    @Autowired
    private org.springframework.boot.jpa.test.autoconfigure.TestEntityManager entityManager;

    @Test
    void should_load_profile_by_id() {
        UUID id = UUID.randomUUID();
        Email email = new Email("load@example.com");
        DisplayName displayName = new DisplayName("Loader");
        Profile profile = new Profile(id, email, displayName);

        saveProfilePort.save(profile);
        entityManager.flush();
        entityManager.clear(); 

        var found = loadProfilePort.findById(new ProfileId(id));

        assertThat(found).isPresent();
        assertThat(found.get().id()).isEqualTo(id);
        assertThat(found.get().email().value()).isEqualTo("load@example.com");
        assertThat(found.get().displayName().value()).isEqualTo("Loader");
    }

    @Test
    void should_return_empty_when_not_found() {
        UUID nonExistentId = UUID.randomUUID();
        
        var found = loadProfilePort.findById(new ProfileId(nonExistentId));

        assertThat(found).isEmpty();
    }
}

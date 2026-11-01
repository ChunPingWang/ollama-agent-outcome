package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;



import org.springframework.test.annotation.DirtiesContext;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({ JpaSaveProfilePort.class, JpaLoadProfilePort.class })
class JpaSaveProfilePortTest {

    @Autowired
    private JpaSaveProfilePort saveProfilePort;

    @Autowired
    private JpaLoadProfilePort loadProfilePort;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void should_save_new_profile() {
        // given
        String id = UUID.randomUUID().toString();
        var profile = Profile.create(
            new ProfileId(id), 
            new Email("new@example.com"), 
            new DisplayName("New User")
        );

        // when
        assertThatCode(() -> saveProfilePort.save(profile)).doesNotThrowAnyException();
        
        // then: verify it's actually in DB
        entityManager.flush();
        entityManager.clear();
        var found = entityManager.find(ProfileEntity.class, id);
        assertThat(found).isNotNull();
        assertThat(found.getEmail()).isEqualTo("new@example.com");
    }

    @Test
    void should_update_existing_profile() {
        // given
        String id = UUID.randomUUID().toString();
        var initialProfile = Profile.create(
            new ProfileId(id), 
            new Email("old@example.com"), 
            new DisplayName("Old Name")
        );
        saveProfilePort.save(initialProfile);
        entityManager.flush();
        entityManager.clear();

        // Load the managed entity to update it (simulating real flow)
        var existing = loadProfilePort.findById(id).orElseThrow();
        existing.update(new Email("updated@example.com"), new DisplayName("Updated Name"));
        
        // when & then
        saveProfilePort.save(existing);
        
        entityManager.flush();
        entityManager.clear();
        var updated = entityManager.find(ProfileEntity.class, id);
        assertThat(updated).isNotNull();
        assertThat(updated.getEmail()).isEqualTo("updated@example.com");
        assertThat(updated.getDisplayName()).isEqualTo("Updated Name");
    }

    @Test
    void should_throw_duplicate_email_exception_on_save() {
        // given
        String id1 = UUID.randomUUID().toString();
        var profile1 = Profile.create(
            new ProfileId(id1), 
            new Email("duplicate@test.com"), 
            new DisplayName("User 1")
        );
        saveProfilePort.save(profile1);
        entityManager.flush();
        entityManager.clear();

        String id2 = UUID.randomUUID().toString();
        var profile2 = Profile.create(
            new ProfileId(id2), 
            new Email("duplicate@test.com"), 
            new DisplayName("User 2")
        );

        // when & then
        assertThatThrownBy(() -> saveProfilePort.save(profile2))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("email already exists");
    }
}

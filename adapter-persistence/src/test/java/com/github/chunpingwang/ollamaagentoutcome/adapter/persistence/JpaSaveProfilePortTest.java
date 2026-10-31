package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import com.github.chunpingwang.ollamaagentoutcome.domain.entity.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.exception.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Age;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.ProfileId;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({ JpaSaveProfilePort.class, ProfileConverter.class })
class JpaSaveProfilePortTest {

    @Autowired
    private JpaSaveProfilePort saveProfilePort;

    @Autowired
    private org.springframework.boot.jpa.test.autoconfigure.TestEntityManager entityManager;

    @Test
    void should_save_profile_successfully() {
        var id = new ProfileId(UUID.randomUUID().toString());
        var name = new Name("John Doe");
        var email = new Email("john@example.com");
        var age = new Age(30);
        var profile = Profile.create(id, name, email, age);

        saveProfilePort.save(profile);
        
        // Flush and clear to ensure data is persisted and cache is cleared for subsequent checks if needed
        entityManager.flush();
        entityManager.clear();

        assertThat(entityManager.find(com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.entity.ProfileEntity.class, id.id())).isNotNull();
    }

    @Test
    void should_throw_duplicate_email_exception_on_duplicate_email() {
        var id1 = new ProfileId(UUID.randomUUID().toString());
        var name1 = new Name("User 1");
        var email = new Email("duplicate@test.com");
        var age1 = new Age(25);
        var profile1 = Profile.create(id1, name1, email, age1);

        saveProfilePort.save(profile1);
        entityManager.flush();
        entityManager.clear();

        var id2 = new ProfileId(UUID.randomUUID().toString());
        var name2 = new Name("User 2");
        var age2 = new Age(26);
        var profile2 = Profile.create(id2, name2, email, age2);

        assertThatThrownBy(() -> saveProfilePort.save(profile2))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("email already exists");
        
        // Verify first record still exists
        entityManager.flush();
        entityManager.clear();
        assertThat(entityManager.find(com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.entity.ProfileEntity.class, id1.id())).isNotNull();
    }
}

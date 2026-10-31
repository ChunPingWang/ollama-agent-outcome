package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.UUID;

import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Age;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.ProfileId;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({ JpaSaveProfilePort.class, JpaLoadProfilePort.class })
class JpaLoadProfilePortTest {

    @Autowired
    private JpaSaveProfilePort saveProfilePort;

    @Autowired
    private JpaLoadProfilePort loadProfilePort;

    @Autowired
    private org.springframework.boot.jpa.test.autoconfigure.TestEntityManager entityManager;

    @Test
    void should_load_profile_by_id() {
        var id = new ProfileId(UUID.randomUUID().toString());
        var name = new Name("John Doe");
        var email = new Email("john@example.com");
        var age = new Age(30);
        var profile = com.github.chunpingwang.ollamaagentoutcome.domain.entity.Profile.create(id, name, email, age);

        saveProfilePort.save(profile);
        entityManager.flush();
        entityManager.clear();

        Optional<com.github.chunpingwang.ollamaagentoutcome.domain.entity.Profile> loaded = loadProfilePort.findById(id.id());

        assertThat(loaded).isPresent();
        assertThat(loaded.get().id()).isEqualTo(id);
        assertThat(loaded.get().name()).isEqualTo(name);
        assertThat(loaded.get().email()).isEqualTo(email);
        assertThat(loaded.get().age()).isEqualTo(age);
    }

    @Test
    void should_return_empty_when_not_found() {
        String nonExistentId = UUID.randomUUID().toString();
        
        Optional<com.github.chunpingwang.ollamaagentoutcome.domain.entity.Profile> result = loadProfilePort.findById(nonExistentId);
        
        assertThat(result).isEmpty();
    }
}

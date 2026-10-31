package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThat;

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
@Import({ JpaSaveProfilePort.class, JpaLoadProfilePort.class, JpaDeleteProfilePort.class })
class JpaDeleteProfilePortTest {

    @Autowired
    private JpaSaveProfilePort saveProfilePort;

    @Autowired
    private JpaLoadProfilePort loadProfilePort;

    @Autowired
    private JpaDeleteProfilePort deleteProfilePort;

    @Autowired
    private org.springframework.boot.jpa.test.autoconfigure.TestEntityManager entityManager;

    @Test
    void should_delete_profile_by_id() {
        var id = new ProfileId(UUID.randomUUID().toString());
        var name = new Name("John Doe");
        var email = new Email("john@example.com");
        var age = new Age(30);
        var profile = com.github.chunpingwang.ollamaagentoutcome.domain.entity.Profile.create(id, name, email, age);

        saveProfilePort.save(profile);
        entityManager.flush();
        entityManager.clear();

        // Verify it exists first
        assertThat(loadProfilePort.findById(id.id())).isPresent();

        deleteProfilePort.deleteById(id.id());
        
        // Flush and clear to ensure deletion is reflected in the persistence context
        entityManager.flush();
        entityManager.clear();

        assertThat(loadProfilePort.findById(id.id())).isEmpty();
    }
}

package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import com.github.chunpingwang.ollamaagentoutcome.domain.Age;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JpaLoadProfilePort.class)
@Transactional
class JpaLoadProfilePortTest {

    @Autowired
    private LoadProfilePort loadProfilePort;

    @Autowired
    private ProfileJpaRepository repository;

    @Test
    void should_load_profile_by_id() {
        // Given
        String id = UUID.randomUUID().toString();
        ProfileEntity entity = new ProfileEntity();
        entity.setId(id);
        entity.setEmail("load@example.com");
        entity.setName("Load User");
        entity.setAge(40);
        entity.setNew(true);
        repository.save(entity);

        // When
        Optional<Profile> profile = loadProfilePort.findById(id);

        // Then
        assertThat(profile).isPresent();
        assertThat(profile.get().id().getValue()).isEqualTo(id);
        assertThat(profile.get().email().getValue()).isEqualTo("load@example.com");
        assertThat(profile.get().name().getValue()).isEqualTo("Load User");
        assertThat(profile.get().age().getValue()).isEqualTo(40);
    }

    @Test
    void should_return_empty_when_profile_not_found() {
        // When
        Optional<Profile> profile = loadProfilePort.findById("non-existent-id");

        // Then
        assertThat(profile).isEmpty();
    }
}

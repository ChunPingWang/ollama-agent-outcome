package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Bio;

/**
 * Unit tests for JpaLoadProfilePort using mocked Repository.
 */
@ExtendWith(MockitoExtension.class)
class JpaLoadProfilePortTest {

    @Mock
    private ProfileJpaRepository repository;

    @InjectMocks
    private JpaLoadProfilePort jpaLoadProfilePort;

    @Test
    void should_load_profile_by_id() {
        UUID id = UUID.randomUUID();
        String name = "John Doe";
        String email = "john@example.com";
        String bio = "Developer";

        ProfileEntity entity = new ProfileEntity(id, name, email, bio);
        
        when(repository.findById(id)).thenReturn(Optional.of(entity));

        Optional<Profile> result = jpaLoadProfilePort.findById(new ProfileId(id));

        assertThat(result).isPresent();
        assertThat(result.get().id().value()).isEqualTo(id);
        assertThat(result.get().name().value()).isEqualTo(name);
        assertThat(result.get().email().value()).isEqualTo(email);
        assertThat(result.get().bio().value()).isEqualTo(bio);
    }

    @Test
    void should_return_empty_when_not_found() {
        UUID id = UUID.randomUUID();

        when(repository.findById(id)).thenReturn(Optional.empty());

        Optional<Profile> result = jpaLoadProfilePort.findById(new ProfileId(id));

        assertThat(result).isEmpty();
    }
}

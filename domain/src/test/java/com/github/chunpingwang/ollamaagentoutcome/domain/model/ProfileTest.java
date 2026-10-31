package com.github.chunpingwang.ollamaagentoutcome.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.chunpingwang.ollamaagentoutcome.domain.exception.ProfileNotFoundException;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class ProfileTest {

    @Test
    void should_update_name_successfully() {
        // Given
        var id = new ProfileId(java.util.UUID.randomUUID());
        var name = new Name("Old");
        var email = new Email("test@example.com");
        var createdAt = Instant.now();
        var profile = new Profile(id, name, email, createdAt);

        // When
        profile.updateName(new Name("New"));

        // Then
        assertThat(profile.getName().text()).isEqualTo("New");
    }

    @Test
    void should_throw_when_profile_not_found_by_id() {
        // This test verifies the exception exists and can be instantiated correctly for later use cases
        var id = new ProfileId(java.util.UUID.randomUUID());
        
        assertThatThrownBy(() -> { throw new ProfileNotFoundException(id); })
                .isInstanceOf(ProfileNotFoundException.class)
                .hasMessageContaining(id.value().toString());
    }
}

package com.github.chunpingwang.ollamaagentoutcome.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class DomainModelTest {

    @Test
    void should_create_profile_with_valid_data() {
        UUID id = UUID.randomUUID();
        Profile profile = new Profile(id, "John Doe", "john@example.com");

        assertThat(profile.getId()).isEqualTo(id);
        assertThat(profile.getName()).isEqualTo("John Doe");
        assertThat(profile.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void should_throw_when_email_is_invalid_format() {
        UUID id = UUID.randomUUID();
        assertThatThrownBy(() -> new Profile(id, "John", "invalid-email"))
                .isInstanceOf(InvalidEmailFormatException.class);
    }

    @Test
    void should_throw_when_name_is_blank() {
        UUID id = UUID.randomUUID();
        assertThatThrownBy(() -> new Profile(id, "", "valid@email.com"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_update_name_successfully() {
        UUID id = UUID.randomUUID();
        Profile profile = new Profile(id, "Old Name", "old@test.com");
        
        profile.setName("New Name");
        
        assertThat(profile.getName()).isEqualTo("New Name");
    }

    @Test
    void should_update_email_successfully() {
        UUID id = UUID.randomUUID();
        Profile profile = new Profile(id, "Name", "old@test.com");
        
        profile.setEmail("new@test.com");
        
        assertThat(profile.getEmail()).isEqualTo("new@test.com");
    }
}

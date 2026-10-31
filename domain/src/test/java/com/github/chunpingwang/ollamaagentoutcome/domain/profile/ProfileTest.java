package com.github.chunpingwang.ollamaagentoutcome.domain.profile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class ProfileTest {

    @Test
    void should_create_profile_with_valid_data() {
        // given
        UUID id = UUID.randomUUID();
        Email email = Email.of("test@example.com");
        DisplayName displayName = DisplayName.of("John Doe");
        Bio bio = Bio.of("Hello World");

        // when
        Profile profile = new Profile(id, email, displayName, bio);

        // then
        assertThat(profile.getId()).isEqualTo(id);
        assertThat(profile.getEmail().address()).isEqualTo("test@example.com");
        assertThat(profile.getDisplayName().name()).isEqualTo("John Doe");
        assertThat(profile.getBio().content()).isEqualTo("Hello World");
        assertThat(profile.getCreatedAt()).isNotNull();
        assertThat(profile.getUpdatedAt()).isEqualTo(profile.getCreatedAt());
        assertThat(profile.isNew()).isTrue();
    }

    @Test
    void should_update_display_name_and_bio() {
        // given
        UUID id = UUID.randomUUID();
        Email email = Email.of("update@example.com");
        DisplayName oldName = DisplayName.of("Old Name");
        Bio oldBio = Bio.of("Old Bio");
        Profile profile = new Profile(id, email, oldName, oldBio);

        Instant beforeUpdate = Instant.now();
        
        DisplayName newName = DisplayName.of("New Name");
        Bio newBio = Bio.of("Updated Bio");

        // when
        profile.update(newName, newBio);

        // then
        assertThat(profile.getDisplayName().name()).isEqualTo("New Name");
        assertThat(profile.getBio().content()).isEqualTo("Updated Bio");
        assertThat(profile.getUpdatedAt()).isAfterOrEqualTo(beforeUpdate);
        assertThat(profile.getEmail().address()).isEqualTo("update@example.com"); // Email unchanged
    }

    @Test
    void should_mark_not_new_after_persist_load_cycle_simulation() {
        // given
        UUID id = UUID.randomUUID();
        Email email = Email.of("persist@example.com");
        DisplayName displayName = DisplayName.of("Persist Test");
        Bio bio = Bio.of(null);
        Profile profile = new Profile(id, email, displayName, bio);

        // when: simulate JPA lifecycle callbacks manually for unit test purposes
        profile.markNotNew(); 

        // then
        assertThat(profile.isNew()).isFalse();
    }
}

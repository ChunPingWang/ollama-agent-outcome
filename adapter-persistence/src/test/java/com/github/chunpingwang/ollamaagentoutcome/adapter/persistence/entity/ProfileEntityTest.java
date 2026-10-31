package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class ProfileEntityTest {

    private final UUID id = UUID.randomUUID();
    private final String email = "test@example.com";
    private final String displayName = "Tester";
    private final String bio = "Hello World";

    @Test
    void should_create_entity_with_all_fields() {
        ProfileEntity entity = new ProfileEntity(id, email, displayName, bio);

        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getEmail()).isEqualTo(email);
        assertThat(entity.getDisplayName()).isEqualTo(displayName);
        assertThat(entity.getBio()).isEqualTo(bio);
    }

    @Test
    void should_allow_null_display_name_and_bio() {
        ProfileEntity entity = new ProfileEntity(id, email, null, null);

        assertThat(entity.getDisplayName()).isNull();
        assertThat(entity.getBio()).isNull();
    }

    @Test
    void should_update_email() {
        ProfileEntity entity = new ProfileEntity(id, email, displayName, bio);
        
        String newEmail = "new@example.com";
        entity.setEmail(newEmail);

        assertThat(entity.getEmail()).isEqualTo(newEmail);
    }

    @Test
    void should_update_display_name() {
        ProfileEntity entity = new ProfileEntity(id, email, displayName, bio);
        
        String newName = "New Name";
        entity.setDisplayName(newName);

        assertThat(entity.getDisplayName()).isEqualTo(newName);
    }

    @Test
    void should_update_bio() {
        ProfileEntity entity = new ProfileEntity(id, email, displayName, bio);
        
        String newBio = "Updated Bio";
        entity.setBio(newBio);

        assertThat(entity.getBio()).isEqualTo(newBio);
    }
}

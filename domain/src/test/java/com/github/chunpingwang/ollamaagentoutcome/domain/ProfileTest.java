package com.github.chunpingwang.ollamaagentoutcome.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class ProfileTest {

    @Test
    void should_create_profile_with_valid_data() {
        UUID idValue = UUID.randomUUID();
        Name name = new Name("John Doe");
        Email email = new Email("john@example.com");
        Bio bio = new Bio("Developer");

        Profile profile = new Profile(new ProfileId(idValue), name, email, bio);

        assertThat(profile.id().value()).isEqualTo(idValue);
        assertThat(profile.name().value()).isEqualTo("John Doe");
        assertThat(profile.email().value()).isEqualTo("john@example.com");
        assertThat(profile.bio().value()).isEqualTo("Developer");
    }

    @Test
    void should_update_name_and_bio() {
        UUID idValue = UUID.randomUUID();
        Name initialName = new Name("Old Name");
        Email email = new Email("old@test.com");
        Bio initialBio = new Bio("Old Bio");

        Profile profile = new Profile(new ProfileId(idValue), initialName, email, initialBio);

        Name newName = new Name("New Name");
        Bio newBio = new Bio("New Bio");

        // Update does not change ID or Email in this simple domain model (Email uniqueness handled elsewhere)
        profile.update(newName, null, newBio); 

        assertThat(profile.name().value()).isEqualTo("New Name");
        assertThat(profile.bio().value()).isEqualTo("New Bio");
        assertThat(profile.email().value()).isEqualTo("old@test.com");
    }

    @Test
    void update_should_allow_null_email_to_keep_existing() {
        UUID idValue = UUID.randomUUID();
        Name name = new Name("User");
        Email existingEmail = new Email("keep@me.com");
        
        Profile profile = new Profile(new ProfileId(idValue), name, existingEmail, new Bio());

        profile.update(name, null, new Bio("Updated"));

        assertThat(profile.email().value()).isEqualTo("keep@me.com");
    }
    
    // Note: More complex validation like "email cannot be changed to duplicate" is handled at Application/Adapter layer.
    // Domain only ensures internal consistency if required. 
}

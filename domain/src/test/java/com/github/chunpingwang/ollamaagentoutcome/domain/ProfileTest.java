package com.github.chunpingwang.ollamaagentoutcome.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class ProfileTest {

    @Test
    void should_create_profile_with_all_fields() {
        ProfileId id = new ProfileId(UUID.randomUUID());
        Email email = new Email("user@example.com");
        Name name = new Name("John Doe");
        Age age = new Age(25);

        Profile profile = new Profile(id, email, name, age);

        assertThat(profile.getId()).isEqualTo(id);
        assertThat(profile.getEmail()).isEqualTo(email);
        assertThat(profile.getName()).isEqualTo(name);
        assertThat(profile.getAge()).isEqualTo(age);
    }

    @Test
    void should_update_profile_and_return_new_instance() {
        ProfileId id = new ProfileId(UUID.randomUUID());
        Email email = new Email("user@example.com");
        Name oldName = new Name("Old Name");
        Age oldAge = new Age(25);

        Profile original = new Profile(id, email, oldName, oldAge);

        Name newName = new Name("New Name");
        Age newAge = new Age(26);

        Profile updated = original.update(newName, newAge);

        // Original should remain unchanged (immutability)
        assertThat(original.getName()).isEqualTo(oldName);
        assertThat(original.getAge()).isEqualTo(oldAge);

        // Updated should have new values
        assertThat(updated.getId()).isEqualTo(id);
        assertThat(updated.getEmail()).isEqualTo(email);
        assertThat(updated.getName()).isEqualTo(newName);
        assertThat(updated.getAge()).isEqualTo(newAge);
    }
}

package com.github.chunpingwang.ollamaagentoutcome.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ProfileTest {

    @Test
    void should_create_profile_with_valid_values() {
        ProfileId id = new ProfileId("550e8400-e29b-41d4-a716-446655440000");
        Email email = new Email("user@example.com");
        Name name = new Name("John Doe");
        Age age = new Age(30);

        Profile profile = new Profile(id, email, name, age);

        assertThat(profile.id()).isEqualTo(id);
        assertThat(profile.email()).isEqualTo(email);
        assertThat(profile.name()).isEqualTo(name);
        assertThat(profile.age()).isEqualTo(age);
    }

    @Test
    void should_create_new_instance_on_update() {
        ProfileId id = new ProfileId("550e8400-e29b-41d4-a716-446655440000");
        Email email = new Email("user@example.com");
        Name oldName = new Name("Old Name");
        Age oldAge = new Age(30);

        Profile original = new Profile(id, email, oldName, oldAge);

        Name newName = new Name("New Name");
        Age newAge = new Age(31);

        Profile updated = original.update(newName, newAge);

        // Original should remain unchanged (immutability)
        assertThat(original.name()).isEqualTo(oldName);
        assertThat(original.age()).isEqualTo(oldAge);

        // Updated should have new values
        assertThat(updated).isNotSameAs(original);
        assertThat(updated.id()).isEqualTo(id);
        assertThat(updated.email()).isEqualTo(email);
        assertThat(updated.name()).isEqualTo(newName);
        assertThat(updated.age()).isEqualTo(newAge);
    }
}

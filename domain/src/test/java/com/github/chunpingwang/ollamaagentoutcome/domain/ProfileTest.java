package com.github.chunpingwang.ollamaagentoutcome.domain;



import static org.assertj.core.api.Assertions.*;import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class ProfileTest {

    @Test
    void should_create_new_profile_with_given_values() {
        String idStr = "550e8400-e29b-41d4-a716-446655440000";
        Email email = new Email("test@example.com");
        DisplayName displayName = new DisplayName("Tester");

        Profile profile = Profile.create(new ProfileId(idStr), email, displayName);

        assertThat(profile.id()).isEqualTo(new ProfileId(idStr));
        assertThat(profile.email()).isEqualTo(email);
        assertThat(profile.displayName()).isEqualTo(displayName);
    }

    @Test
    void should_update_display_name_and_email() {
        String idStr = UUID.randomUUID().toString();
        Profile profile = Profile.create(
            new ProfileId(idStr),
            new Email("old@test.com"),
            new DisplayName("Old Name")
        );

        profile.update(new Email("new@test.com"), new DisplayName("New Name"));

        assertThat(profile.email()).isEqualTo(new Email("new@test.com"));
        assertThat(profile.displayName()).isEqualTo(new DisplayName("New Name"));
    }
}

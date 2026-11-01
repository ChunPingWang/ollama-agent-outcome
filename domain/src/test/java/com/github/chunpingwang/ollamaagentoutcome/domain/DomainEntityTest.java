package com.github.chunpingwang.ollamaagentoutcome.domain;


import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class DomainEntityTest {

    private final UUID testId = UUID.randomUUID();

    @Test
    void should_create_profile_with_valid_data() {
        Profile profile = new Profile(testId, "user@example.com", "Display Name");
        
        assertThat(profile.id().id()).isEqualTo(testId);
        assertThat(profile.getEmail().value()).isEqualTo("user@example.com");
        assertThat(profile.getDisplayName().value()).isEqualTo("Display Name");
    }

    @Test
    void should_reject_invalid_email_on_creation() {
        assertThatThrownBy(() -> new Profile(testId, "invalid-email", "Name"))
                .isInstanceOf(InvalidEmailException.class);
    }

    @Test
    void should_update_display_name_successfully() {
        Profile profile = new Profile(testId, "user@example.com", "Old Name");
        profile.updateDisplayName("New Name");
        
        assertThat(profile.getDisplayName().value()).isEqualTo("New Name");
    }

    @Test
    void should_set_null_when_updating_display_name_to_blank() {
        Profile profile = new Profile(testId, "user@example.com", "Old Name");
        profile.updateDisplayName("");
        
        assertThat(profile.getDisplayName().isPresent()).isFalse();
    }

    @Test
    void should_equality_based_on_id() {
        UUID id1 = UUID.randomUUID();
        Profile p1a = new Profile(id1, "a@test.com", "A");
        Profile p1b = new Profile(id1, "other@test.com", "B"); // Different email/name
        
        assertThat(p1a).isEqualTo(p1b);
        assertThat(p1a.hashCode()).isEqualTo(p1b.hashCode());
    }
}

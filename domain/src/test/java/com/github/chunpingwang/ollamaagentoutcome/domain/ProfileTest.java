package com.github.chunpingwang.ollamaagentoutcome.domain;



import static org.assertj.core.api.Assertions.*;import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class ProfileTest {

    private final UUID testId = UUID.randomUUID();
    
    @Test
    void should_create_profile_with_all_fields() {
        ProfileId id = new ProfileId(testId);
        Email email = new Email("new@example.com");
        DisplayName name = new DisplayName("New User");
        
        Profile profile = new Profile(id, email, name);
        
        assertThat(profile.getId().getValue()).isEqualTo(testId);
        assertThat(profile.getEmail().getAddress()).isEqualTo("new@example.com");
        assertThat(profile.getDisplayName().getName()).isEqualTo("New User");
    }

    @Test
    void should_update_display_name() {
        ProfileId id = new ProfileId(testId);
        Email email = new Email("user@test.com");
        Profile profile = new Profile(id, email, new DisplayName("Old Name"));
        
        profile.updateDisplayName("New Name");
        
        assertThat(profile.getDisplayName().getName()).isEqualTo("New Name");
    }

    @Test
    void should_clear_display_name_when_blank() {
        ProfileId id = new ProfileId(testId);
        Email email = new Email("user@test.com");
        Profile profile = new Profile(id, email, new DisplayName("Name"));
        
        profile.updateDisplayName("");
        
        assertThat(profile.getDisplayName().getName()).isNull();
    }

    @Test
    void should_reject_null_id_on_creation() {
        assertThatThrownBy(() -> new Profile(null, null, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("ID cannot be null");
    }

    @Test
    void profiles_are_equal_by_id() {
        UUID sameId = UUID.randomUUID();
        Profile p1 = new Profile(new ProfileId(sameId), new Email("a@b.c"), new DisplayName("A"));
        Profile p2 = new Profile(new ProfileId(sameId), new Email("x@y.z"), new DisplayName("B"));
        
        assertThat(p1).isEqualTo(p2);
        assertThat(p1.hashCode()).isEqualTo(p2.hashCode());
    }

    @Test
    void profiles_with_different_ids_are_not_equal() {
        Profile p1 = new Profile(new ProfileId(UUID.randomUUID()), new Email("a@b.c"), new DisplayName("A"));
        Profile p2 = new Profile(new ProfileId(UUID.randomUUID()), new Email("a@b.c"), new DisplayName("A"));
        
        assertThat(p1).isNotEqualTo(p2);
    }
}

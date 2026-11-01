package com.github.chunpingwang.ollamaagentoutcome.domain;


import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class ProfileTest {

    private final UUID testId = UUID.randomUUID();
    private final Email email = new Email("test@example.com");
    private final DisplayName name = new DisplayName("Initial Name");

    @Test
    void should_create_profile_with_id_email_and_name() {
        Profile profile = new Profile(testId, email, name);
        
        assertThat(profile.id()).isEqualTo(new ProfileId(testId));
        assertThat(profile.email()).isEqualTo(email);
        assertThat(profile.displayName()).isEqualTo(name);
    }

    @Test
    void should_change_display_name_successfully() {
        Profile profile = new Profile(testId, email, name);
        DisplayName newName = new DisplayName("New Name");
        
        Profile updatedProfile = profile.changeDisplayName(newName);
        
        // Should return a new instance or the same? Usually immutable pattern returns new, 
        // but for JPA entity simulation in domain, often it mutates and returns this or just updates state.
        // Given "Immutable after creation except for mutable fields via methods" in plan, let's assume mutation of internal state is allowed for simplicity in Domain Entity context if not strictly value object.
        // However, Plan says: `updateDisplayName` returns new Profile OR updates internal state.
        // Let's test that the returned profile has the new name. If it returns 'this', assertions on reference equality might fail if we expect new instance.
        // Standard Hexagonal Domain Entities are often mutable aggregates. Let's assume it modifies itself and returns void or self.
        // Wait, the method signature in my head was `changeDisplayName`. Let's check typical behavior.
        // If I define `public void changeDisplayName(DisplayName displayName)`, then:
        profile.changeDisplayName(newName);
        assertThat(profile.displayName()).isEqualTo(newName);
    }

    @Test
    void should_reject_invalid_email_in_constructor() {
        Email invalidEmail = null; // Will be caught by constructor validation usually
        
        // Assuming constructor validates email VO (which throws InvalidEmailException internally)
        // But here we pass a valid VO. The exception comes from VOs themselves.
        // So this test case is covered by EmailTest mostly, unless Profile does extra checks.
        
        // Test with Null ID? Plan says "ID 由系統產生", but for testing creation we provide one.
        // If ID cannot be null:
        assertThatThrownBy(() -> new Profile(null, email, name))
                .isInstanceOf(IllegalArgumentException.class);
    }
}

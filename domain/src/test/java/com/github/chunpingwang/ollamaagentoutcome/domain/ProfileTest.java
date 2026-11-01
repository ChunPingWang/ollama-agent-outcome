package com.github.chunpingwang.ollamaagentoutcome.domain;



import static org.assertj.core.api.Assertions.*;import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class ProfileTest {

    private final UUID testId = UUID.randomUUID();
    // Valid email for testing profile construction logic, not Email validation itself
    private final Email validEmail = new Email("test@example.com"); 
    private final DisplayName name = new DisplayName("Initial Name");

    @Test
    void should_create_profile_with_valid_data() {
        Profile profile = new Profile(testId, validEmail, name);
        
        assertThat(profile.id()).isEqualTo(testId);
        assertThat(profile.email()).isEqualTo(validEmail);
        assertThat(profile.displayName()).isEqualTo(name);
    }

    @Test
    void should_reject_null_id() {
        // Profile constructor uses Objects.requireNonNull which throws NullPointerException
        assertThatThrownBy(() -> new Profile(null, validEmail, name))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("id cannot be null");
    }

    @Test
    void should_update_display_name_successfully() {
        Profile profile = new Profile(testId, validEmail, name);
        DisplayName newName = new DisplayName("New Name");
        
        Profile updatedProfile = profile.changeDisplayName(newName);
        
        // Should return a new instance or the same instance with updated state. 
        // Assuming immutable pattern for domain objects in this context:
        assertThat(updatedProfile).isNotSameAs(profile);
        assertThat(updatedProfile.displayName().value()).isEqualTo("New Name");
        assertThat(updatedProfile.email()).isEqualTo(validEmail);
        assertThat(updatedProfile.id()).isEqualTo(testId);
    }

    @Test
    void should_not_allow_invalid_email_in_constructor() {
        // Since Email VO validates format on construction, attempting to create an invalid email throws InvalidEmailException.
        // We verify this behavior. Note: This technically tests Email, not Profile, but ensures the system rejects bad emails at entry.
        assertThatThrownBy(() -> new Email("invalid-email"))
                .isInstanceOf(InvalidEmailException.class)
                .hasMessageContaining("invalid format");
            
        // Additionally, we can assert that a valid profile CAN be created with a valid email.
        // (Already covered by other tests).
    }
}

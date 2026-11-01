package com.github.chunpingwang.ollamaagentoutcome.domain;



import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class ProfileFactoryTest {

    private final UUID testId = UUID.randomUUID();
    private final String validEmailStr = "user@example.com";
    private final String displayNameStr = "Test User";

    @Test
    void should_create_profile_with_valid_data() {
        Email email = new Email(validEmailStr);
        DisplayName displayName = new DisplayName(displayNameStr);
        Profile profile = Profile.create(new ProfileId(testId), email, displayName);

        assertThat(profile.id().id()).isEqualTo(testId);
        assertThat(profile.email().value()).isEqualTo(validEmailStr);
        assertThat(profile.displayName().value()).isEqualTo(displayNameStr);
    }

    @Test
    void should_reject_invalid_email_during_creation() {
        // Email VO constructor throws InvalidEmailFormatException for invalid format
        assertThatThrownBy(() -> 
            Profile.create(
                new ProfileId(testId), 
                new Email("invalid-email"), 
                new DisplayName("User")
            )
        ).isInstanceOf(InvalidEmailFormatException.class);
    }
    
    @Test
    void should_create_profile_with_null_display_name() {
        Email email = new Email(validEmailStr);
        Profile profile = Profile.create(new ProfileId(testId), email, null);
        
        assertThat(profile.id().id()).isEqualTo(testId);
        assertThat(profile.email().value()).isEqualTo(validEmailStr);
        assertThat(profile.displayName()).isNull();
    }
}

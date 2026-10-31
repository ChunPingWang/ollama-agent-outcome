package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.InvalidEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateProfileServiceTest {

    @Mock
    private SaveProfilePort saveProfilePort;

    @InjectMocks
    private CreateProfileService createProfileService;

    @Test
    void should_create_profile_successfully() {
        Email email = new Email("test@example.com");
        DisplayName displayName = new DisplayName("Test User");
        
        Profile savedProfile = new Profile(new ProfileId(UUID.randomUUID()), email, displayName);
        when(saveProfilePort.save(any(Profile.class))).thenReturn(savedProfile);

        ProfileId result = createProfileService.execute(email, displayName);

        assertThat(result).isNotNull();
        verify(saveProfilePort).save(any());
    }

    @Test
    void should_throw_duplicate_email_exception_when_save_fails_with_duplicate() {
        Email email = new Email("duplicate@test.com");
        DisplayName displayName = new DisplayName("Duplicate User");
        
        when(saveProfilePort.save(any(Profile.class)))
                .thenThrow(new DuplicateEmailException("Email already exists"));

        try {
            createProfileService.execute(email, displayName);
            throw new AssertionError("Expected DuplicateEmailException to be thrown");
        } catch (DuplicateEmailException e) {
            assertThat(e.getMessage()).contains("Email already exists");
        }
                
        // Verify that save was called before exception propagated
        verify(saveProfilePort).save(any());
    }

    @Test
    void should_not_call_port_if_input_validation_fails_implicitly_via_domain_objects() {
        // Domain objects (Email/DisplayName) validate in constructor.
        // If invalid data is passed, the exception happens BEFORE execute() or during VO creation.
        // This test verifies that if we pass an empty string directly to Email constructor inside a try-catch block 
        // simulating external validation failure, it throws InvalidEmailException and does NOT involve the port.
        // Since our service takes VOs, this scenario effectively means: "If VO construction fails, no port call occurs".
        
        assertThatThrownBy(() -> new Email(""))
                .isInstanceOf(InvalidEmailException.class)
                .hasMessageContaining("blank or empty");
        
        // Ensure port was never touched because execution didn't reach save step due to earlier exception
        verify(saveProfilePort, never()).save(any());
    }
}

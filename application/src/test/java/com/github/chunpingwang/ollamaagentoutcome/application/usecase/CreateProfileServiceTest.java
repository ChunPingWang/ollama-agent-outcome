package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.InvalidEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
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
        verify(saveProfilePort).save(argThat(profile -> 
            profile.getEmail().equals(email) && 
            profile.getDisplayName().equals(displayName)
        ));
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
        InvalidEmailException thrown = org.junit.jupiter.api.Assertions.assertThrows(InvalidEmailException.class, () -> {
            createProfileService.execute(new Email(""), new DisplayName("Valid Name"));
        });
        
        assertThat(thrown).hasMessageContaining("cannot be blank or empty");
        verifyNoInteractions(saveProfilePort);
    }
}

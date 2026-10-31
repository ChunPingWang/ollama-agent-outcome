package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase.CreateProfileRequest;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.ExistsByEmailPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

@ExtendWith(MockitoExtension.class)
class CreateProfileServiceTest {

    @Mock
    private SaveProfilePort saveProfilePort;

    @Mock
    private ExistsByEmailPort existsByEmailPort;

    @InjectMocks
    private CreateProfileService createProfileService;

    @Test
    void should_create_profile_when_email_is_unique() {
        var email = new Email("test@example.com");
        var displayName = new DisplayName("John Doe");
        var request = new CreateProfileRequest(email, displayName);

        when(existsByEmailPort.existsByEmail(email)).thenReturn(false);

        createProfileService.execute(request);

        verify(saveProfilePort).save(org.mockito.ArgumentMatchers.argThat(profile -> {
            return profile.email().equals(email) && profile.displayName().equals(displayName);
        }));
    }

    @Test
    void should_throw_duplicate_exception_when_email_exists() {
        var email = new Email("duplicate@example.com");
        var displayName = new DisplayName("Jane Doe");
        var request = new CreateProfileRequest(email, displayName);

        when(existsByEmailPort.existsByEmail(email)).thenReturn(true);

        assertThatThrownBy(() -> createProfileService.execute(request))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("Duplicate email");
        
        verify(saveProfilePort, org.mockito.Mockito.never()).save(org.mockito.ArgumentMatchers.any());
    }
}

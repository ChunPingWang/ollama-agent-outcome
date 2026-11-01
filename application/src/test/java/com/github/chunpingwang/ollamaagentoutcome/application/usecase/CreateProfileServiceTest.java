package com.github.chunpingwang.ollamaagentoutcome.application.usecase;







import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase.CreateProfileRequest;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class CreateProfileServiceTest {

    @Mock
    private SaveProfilePort saveProfilePort;

    @InjectMocks
    private CreateProfileService createProfileService;

    @Test
    void should_call_save_port_once_on_success() {
        // given
        String email = "user@example.com";
        String displayName = "Test User";
        CreateProfileRequest request = new CreateProfileRequest(email, displayName);

        doNothing().when(saveProfilePort).save(argThat(profile -> 
            profile.email().address().equals(email) && 
            (profile.displayName() == null || profile.displayName().name().equals(displayName))
        ));

        // when
        createProfileService.execute(request);

        // then
        verify(saveProfilePort).save(argThat(profile -> 
            profile.email().address().equals(email) && 
            (profile.displayName() == null || profile.displayName().name().equals(displayName))
        ));
    }

    @Test
    void should_propagate_duplicate_email_exception() {
        // given
        String email = "duplicate@test.com";
        CreateProfileRequest request = new CreateProfileRequest(email, "User");

        doThrow(new DuplicateEmailException(email)).when(saveProfilePort).save(org.mockito.ArgumentMatchers.any(Profile.class));

        // when & then
        assertThatThrownBy(() -> createProfileService.execute(request))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining(email);
        
        verify(saveProfilePort).save(org.mockito.ArgumentMatchers.any(Profile.class));
    }
}

package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileCommand;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.profile.Bio;
import com.github.chunpingwang.ollamaagentoutcome.domain.profile.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.profile.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.profile.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.profile.exception.DuplicateEmailException;

@ExtendWith(MockitoExtension.class)
class CreateProfileServiceTest {

    @Mock
    private LoadProfilePort loadProfilePort;

    @Mock
    private SaveProfilePort saveProfilePort;

    @InjectMocks
    private CreateProfileService createProfileService;

    @Test
    void should_create_profile_when_email_is_unique() {
        // given
        String email = "new@example.com";
        String displayName = "New User";
        String bio = "Hello World";
        
        CreateProfileCommand command = new CreateProfileCommand(email, displayName, bio);
        
        when(loadProfilePort.findByEmail(any())).thenReturn(Optional.empty());
        doNothing().when(saveProfilePort).save(any(Profile.class));

        // when
        createProfileService.execute(command);

        // then
        verify(loadProfilePort).findByEmail(email);
        verify(saveProfilePort).save(any(Profile.class));
    }

    @Test
    void should_throw_duplicate_exception_when_email_exists() {
        // given
        String existingEmail = "existing@example.com";
        CreateProfileCommand command = new CreateProfileCommand(existingEmail, "User", null);
        
        Profile existingProfile = new Profile(
                UUID.randomUUID(), 
                Email.of(existingEmail), 
                DisplayName.of("Existing"), 
                Bio.of(null)
        );
        
        when(loadProfilePort.findByEmail(existingEmail)).thenReturn(Optional.of(existingProfile));

        // when & then
        assertThatThrownBy(() -> createProfileService.execute(command))
            .isInstanceOf(DuplicateEmailException.class)
            .hasMessageContaining("Duplicate email");
            
        verify(loadProfilePort).findByEmail(existingEmail);
        verify(saveProfilePort, never()).save(any());
    }
}

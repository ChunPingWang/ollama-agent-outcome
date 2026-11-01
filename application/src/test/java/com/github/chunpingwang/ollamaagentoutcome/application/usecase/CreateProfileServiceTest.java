package com.github.chunpingwang.ollamaagentoutcome.application.usecase;



import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.ExistsByEmailPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class CreateProfileServiceTest {

    @Mock
    private ExistsByEmailPort existsByEmailPort;

    @Mock
    private SaveProfilePort saveProfilePort;

    @InjectMocks
    private CreateProfileService createProfileService;

    @Test
    void should_create_profile_successfully() {
        // Given
        String email = "new@example.com";
        String displayName = "New User";
        UUID newId = UUID.randomUUID();
        
        when(existsByEmailPort.existsByEmail(any(Email.class))).thenReturn(false);
        when(saveProfilePort.save(any(Profile.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        ProfileId resultId = createProfileService.execute(email, displayName);

        // Then
        assertThat(resultId).isNotNull();
        verify(existsByEmailPort).existsByEmail(new Email(email));
        verify(saveProfilePort).save(any(Profile.class));
    }

    @Test
    void should_throw_duplicate_email_exception_when_email_exists() {
        // Given
        String email = "existing@example.com";
        String displayName = "Existing User";
        
        when(existsByEmailPort.existsByEmail(any(Email.class))).thenReturn(true);

        // When & Then
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> 
            createProfileService.execute(email, displayName)
        ).isInstanceOf(DuplicateEmailException.class)
         .hasMessageContaining("already exists");
         
        verify(existsByEmailPort).existsByEmail(new Email(email));
        verify(saveProfilePort, org.mockito.Mockito.never()).save(any());
    }
}

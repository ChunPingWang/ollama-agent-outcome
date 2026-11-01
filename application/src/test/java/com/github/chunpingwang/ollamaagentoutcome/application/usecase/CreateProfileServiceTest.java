package com.github.chunpingwang.ollamaagentoutcome.application.usecase;



import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileCommand;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.ExistsByEmailPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;

@MockitoSettings(strictness = Strictness.LENIENT)
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
        // Given
        String email = "newuser@example.com";
        String displayName = "New User";
        CreateProfileCommand command = new CreateProfileCommand(email, displayName);

        when(existsByEmailPort.existsByEmail(email)).thenReturn(false);
        
        Profile expectedProfile = new Profile(UUID.randomUUID().toString(), new Email(email), new DisplayName(displayName));
        when(saveProfilePort.save(any(Profile.class))).thenReturn(expectedProfile);

        // When
        Profile result = createProfileService.execute(command);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.email()).isEqualTo(new Email(email));
        verify(saveProfilePort).save(any(Profile.class));
    }

    @Test
    void should_throw_duplicate_exception_when_email_exists() {
        // Given
        String email = "existing@example.com";
        String displayName = "Existing User";
        CreateProfileCommand command = new CreateProfileCommand(email, displayName);

        when(existsByEmailPort.existsByEmail(email)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> createProfileService.execute(command))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("電子郵件已存在");

        verify(saveProfilePort, never()).save(any());
    }
}

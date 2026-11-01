package com.github.chunpingwang.ollamaagentoutcome.application.usecase;



import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
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
    void should_create_profile_when_email_is_unique() {
        Email email = new Email("new@example.com");
        DisplayName displayName = new DisplayName("New User");

        when(existsByEmailPort.existsByEmail(email)).thenReturn(false);

        // Should not throw any exception on successful creation
        assertThatCode(() -> createProfileService.execute(email, displayName))
                .doesNotThrowAnyException();
        
        verify(saveProfilePort).save(org.mockito.ArgumentMatchers.any());
    }
    
    @Test
    void should_throw_duplicate_exception_when_email_exists() {
        Email email = new Email("existing@example.com");
        DisplayName displayName = new DisplayName("Existing User");

        when(existsByEmailPort.existsByEmail(email)).thenReturn(true);

        assertThatThrownBy(() -> createProfileService.execute(email, displayName))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("Email already exists");
        
        // Verify that save was NOT called because duplicate check failed first
        verifyNoInteractions(saveProfilePort);
    }
}

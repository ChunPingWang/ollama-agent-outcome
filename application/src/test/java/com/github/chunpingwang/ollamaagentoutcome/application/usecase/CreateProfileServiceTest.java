package com.github.chunpingwang.ollamaagentoutcome.application.usecase;



import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileRequest;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.ExistsByEmailPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
        Email email = new Email("new@example.com");
        DisplayName displayName = new DisplayName("New User");
        CreateProfileRequest request = new CreateProfileRequest(email, displayName);

        when(existsByEmailPort.existsByEmail(any(Email.class))).thenReturn(false);

        createProfileService.execute(request);

        verify(saveProfilePort).save(any(Profile.class));
    }

    @Test
    void should_throw_duplicate_exception_when_email_exists() {
        Email email = new Email("existing@example.com");
        DisplayName displayName = new DisplayName("Existing User");
        CreateProfileRequest request = new CreateProfileRequest(email, displayName);

        when(existsByEmailPort.existsByEmail(any(Email.class))).thenReturn(true);

        assertThatThrownBy(() -> createProfileService.execute(request))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("already exists");

        verify(saveProfilePort, never()).save(any());
    }
}

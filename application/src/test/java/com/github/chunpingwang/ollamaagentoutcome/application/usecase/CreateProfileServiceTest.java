package com.github.chunpingwang.ollamaagentoutcome.application.usecase;






import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase.CreateProfileRequest;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
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

    @InjectMocks
    private CreateProfileService createProfileService;

    @Test
    void should_call_save_port_once_on_success() {
        // given
        String email = "user@example.com";
        String displayName = "Test User";
        CreateProfileRequest request = new CreateProfileRequest(email, displayName);

        // when
        createProfileService.execute(request);

        // then
        verify(saveProfilePort).save(any(Profile.class));
    }

    @Test
    void should_propagate_duplicate_email_exception_from_port() {
        // given
        String email = "duplicate@test.com";
        String displayName = "Duplicate User";
        CreateProfileRequest request = new CreateProfileRequest(email, displayName);
        
        doThrow(new DuplicateEmailException("email already exists"))
            .when(saveProfilePort).save(any());

        // when & then
        assertThatThrownBy(() -> createProfileService.execute(request))
            .isInstanceOf(DuplicateEmailException.class)
            .hasMessageContaining("email already exists");
    }
}

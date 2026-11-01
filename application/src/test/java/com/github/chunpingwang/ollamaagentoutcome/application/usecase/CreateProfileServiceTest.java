package com.github.chunpingwang.ollamaagentoutcome.application.usecase;



import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

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
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileRequest;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;

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
        String displayName = "John Doe";
        CreateProfileRequest request = new CreateProfileRequest(email, displayName);

        // when
        createProfileService.execute(request);

        // then
        verify(saveProfilePort).save(any(Profile.class));
    }

    @Test
    void should_propagate_duplicate_email_exception() {
        // given
        String email = "duplicate@example.com";
        CreateProfileRequest request = new CreateProfileRequest(email, "User");
        
        doThrow(new DuplicateEmailException("Duplicate email: " + email))
            .when(saveProfilePort).save(any(Profile.class));

        // when & then
        assertThatThrownBy(() -> createProfileService.execute(request))
            .isInstanceOf(DuplicateEmailException.class)
            .hasMessageContaining("Duplicate email");
    }
}

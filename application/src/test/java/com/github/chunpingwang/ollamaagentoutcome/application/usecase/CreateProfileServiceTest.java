package com.github.chunpingwang.ollamaagentoutcome.application.usecase;



import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class CreateProfileServiceTest {

    @Mock
    private SaveProfilePort saveProfilePort;

    @InjectMocks
    private CreateProfileService createProfileService;

    @Test
    void should_create_profile_and_save_when_valid() {
        String id = UUID.randomUUID().toString();
        String emailAddr = "test@example.com";
        String displayNameStr = "Test User";

        // Act & Assert: 驗證不會拋出例外，且 port 被呼叫
        createProfileService.execute(emailAddr, displayNameStr);

        verify(saveProfilePort).save(any(Profile.class));
    }

    @Test
    void should_throw_duplicate_email_exception_when_port_throws_it() {
        String emailAddr = "duplicate@example.com";

        doThrow(new DuplicateEmailException("email already exists"))
            .when(saveProfilePort).save(any(Profile.class));

        assertThatThrownBy(() -> createProfileService.execute(emailAddr, "User"))
            .isInstanceOf(DuplicateEmailException.class)
            .hasMessageContaining("email already exists");

        verify(saveProfilePort).save(any(Profile.class));
    }
}

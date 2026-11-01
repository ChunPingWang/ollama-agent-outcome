package com.github.chunpingwang.ollamaagentoutcome.application.usecase;



import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.InvalidEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
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
    void should_save_profile_when_input_is_valid() {
        String email = "user@example.com";
        String displayName = "Test User";

        createProfileService.execute(email, displayName);

        verify(saveProfilePort).save(any(Profile.class));
    }

    @Test
    void should_throw_duplicate_email_exception_when_port_throws_it() {
        when(saveProfilePort.save(any())).thenThrow(new DuplicateEmailException("Duplicate"));

        assertThatThrownBy(() -> createProfileService.execute("dup@test.com", "User"))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("Duplicate");
    }

    @Test
    void should_not_call_port_when_email_is_invalid() {
        // Invalid email format throws InvalidEmailException in Email constructor (Domain layer)
        // This happens before calling the port.
        
        assertThatThrownBy(() -> createProfileService.execute("invalid-email", "User"))
                .isInstanceOf(InvalidEmailException.class);

        verify(saveProfilePort, never()).save(any());
    }
}

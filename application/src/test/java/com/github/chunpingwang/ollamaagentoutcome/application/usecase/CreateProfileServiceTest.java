package com.github.chunpingwang.ollamaagentoutcome.application.usecase;



import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class CreateProfileServiceTest {

    @Mock
    private SaveProfilePort saveProfilePort;

    @InjectMocks
    private CreateProfileService createProfileService;

    @Test
    void should_save_profile_when_valid_input() {
        String email = "user@example.com";
        String displayName = "John Doe";

        createProfileService.execute(email, displayName);

        verify(saveProfilePort).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void should_throw_duplicate_email_exception_when_port_throws_it() {
        String email = "duplicate@example.com";
        String displayName = "Duplicate User";

        doThrow(new DuplicateEmailException("email already exists"))
            .when(saveProfilePort).save(org.mockito.ArgumentMatchers.any());

        assertThatThrownBy(() -> createProfileService.execute(email, displayName))
            .isInstanceOf(DuplicateEmailException.class)
            .hasMessageContaining("email already exists");
    }
}

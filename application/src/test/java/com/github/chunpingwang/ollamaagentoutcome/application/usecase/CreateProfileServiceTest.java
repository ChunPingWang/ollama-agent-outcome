package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chunpingwang.ollamaagentoutcome.application.command.CreateProfileCommand;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.ValidateEmailUniquePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.exception.DuplicateEmailException;

@ExtendWith(MockitoExtension.class)
class CreateProfileServiceTest {

    @Mock
    private SaveProfilePort saveProfilePort;

    @Mock
    private ValidateEmailUniquePort validateEmailUniquePort;

    @InjectMocks
    private CreateProfileService createProfileService;

    @Test
    void should_create_profile_successfully() {
        var command = new CreateProfileCommand("user@example.com", "John Doe", "Hello World");
        
        when(validateEmailUniquePort.isUnique(any(), any())).thenReturn(true);
        doNothing().when(saveProfilePort).save(any());

        createProfileService.execute(command);

        verify(validateEmailUniquePort).isUnique("user@example.com", null);
        verify(saveProfilePort).save(any());
    }

    @Test
    void should_throw_duplicate_email_exception_when_email_exists() {
        var command = new CreateProfileCommand("duplicate@test.com", "User", "");
        
        when(validateEmailUniquePort.isUnique(any(), any())).thenReturn(false);

        assertThatThrownBy(() -> createProfileService.execute(command))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("Duplicate email: duplicate@test.com");
    }
}

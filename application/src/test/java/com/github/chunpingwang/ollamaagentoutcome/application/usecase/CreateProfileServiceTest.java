package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

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

import com.github.chunpingwang.ollamaagentoutcome.application.command.CreateProfileCommand;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.exception.DuplicateEmailException;

@ExtendWith(MockitoExtension.class)
class CreateProfileServiceTest {

    @Mock
    private SaveProfilePort saveProfilePort;

    @InjectMocks
    private CreateProfileService createProfileService;

    @Test
    void should_create_profile_successfully() {
        String email = "user@example.com";
        String displayName = "Tester";
        String bio = "Hello World";

        CreateProfileCommand command = new CreateProfileCommand(email, displayName, bio);

        createProfileService.execute(command);

        verify(saveProfilePort).save(any());
    }

    @Test
    void should_throw_duplicate_email_exception_when_email_exists() {
        String email = "duplicate@test.com";
        
        CreateProfileCommand command = new CreateProfileCommand(email, "User", "Bio");

        doThrow(new DuplicateEmailException(email)).when(saveProfilePort).save(any());

        assertThatThrownBy(() -> createProfileService.execute(command))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("Duplicate email address: " + email);
    }
}

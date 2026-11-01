package com.github.chunpingwang.ollamaagentoutcome.application.usecase;





import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase.CreateProfileCommand;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class CreateProfileServiceTest {

    @Mock
    private SaveProfilePort saveProfilePort;

    private CreateProfileService service;

    @BeforeEach
    void setUp() {
        service = new CreateProfileService(saveProfilePort);
    }

    @Test
    void should_create_profile_and_return_id() {
        // Arrange
        String nameValue = "John Doe";
        String emailValue = "john@example.com";
        UUID expectedUuid = UUID.randomUUID();
        
        when(saveProfilePort.save(any(Profile.class)))
            .thenReturn(new ProfileId(expectedUuid));

        // Act
        CreateProfileCommand command = new CreateProfileCommand(nameValue, emailValue);
        ProfileId resultId = service.execute(command);

        // Assert
        assertThat(resultId.id()).isEqualTo(expectedUuid);
        verify(saveProfilePort).save(argThat(profile -> 
            profile.name().value().equals(nameValue) && 
            profile.email().value().equals(emailValue)
        ));
    }

    @Test
    void should_throw_duplicate_email_exception_when_save_fails() {
        // Arrange
        String duplicateEmail = "dup@example.com";
        doThrow(new DuplicateEmailException())
            .when(saveProfilePort).save(any(Profile.class));

        // Act & Assert
        CreateProfileCommand command = new CreateProfileCommand("New User", duplicateEmail);
        
        org.assertj.core.api.ThrowableAssert.ThrowingCallable callable = () -> service.execute(command);
        
        assertThatThrownBy(callable)
            .isInstanceOf(DuplicateEmailException.class)
            .hasMessageContaining("email already exists");
            
        // Verify save was attempted once before exception propagated
        verify(saveProfilePort, times(1)).save(any());
    }
}

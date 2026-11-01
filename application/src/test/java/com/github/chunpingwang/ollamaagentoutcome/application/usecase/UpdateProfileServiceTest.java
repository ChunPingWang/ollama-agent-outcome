package com.github.chunpingwang.ollamaagentoutcome.application.usecase;




import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase.UpdateProfileCommand;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class UpdateProfileServiceTest {

    @Mock
    private LoadProfilePort loadProfilePort;

    @Mock
    private SaveProfilePort saveProfilePort;

    private UpdateProfileService service;

    @BeforeEach
    void setUp() {
        service = new UpdateProfileService(loadProfilePort, saveProfilePort);
    }

    @Test
    void should_update_profile_when_exists() {
        // Arrange
        UUID idValue = UUID.randomUUID();
        ProfileId profileId = new ProfileId(idValue);
        Name oldName = new Name("Old Name");
        Email email = new Email("old@example.com");
        
        when(loadProfilePort.findById(profileId))
            .thenReturn(Optional.of(new Profile(profileId, oldName, email)));

        String newName = "New Name";
        String newEmail = "new@example.com";

        // Act
        UpdateProfileCommand command = new UpdateProfileCommand(profileId, newName, newEmail);
        service.execute(command);

        // Assert
        verify(saveProfilePort).save(argThat(p -> 
            p.id().equals(profileId) && 
            p.name().value().equals(newName) && 
            p.email().value().equals(newEmail)
        ));
    }

    @Test
    void should_throw_not_found_exception_when_profile_does_not_exist() {
        // Arrange
        UUID nonExistentUuid = UUID.randomUUID();
        ProfileId profileId = new ProfileId(nonExistentUuid);
        
        when(loadProfilePort.findById(profileId)).thenReturn(Optional.empty());

        // Act & Assert
        org.assertj.core.api.ThrowableAssert.ThrowingCallable callable = () -> 
            service.execute(new UpdateProfileCommand(profileId, "New", "test@test.com"));
            
        assertThatThrownBy(callable)
            .isInstanceOf(ProfileNotFoundException.class)
            .hasMessageContaining(nonExistentUuid.toString());
            
        verify(loadProfilePort).findById(profileId);
        verifyNoInteractions(saveProfilePort);
    }

    @Test
    void should_propagate_duplicate_email_exception_from_save_port() {
        // Arrange
        UUID idValue = UUID.randomUUID();
        ProfileId profileId = new ProfileId(idValue);
        Name name = new Name("Existing");
        Email email = new Email("existing@example.com");
        
        when(loadProfilePort.findById(profileId))
            .thenReturn(Optional.of(new Profile(profileId, name, email)));

        doThrow(new DuplicateEmailException())
            .when(saveProfilePort).save(any(Profile.class));

        // Act & Assert
        org.assertj.core.api.ThrowableAssert.ThrowingCallable callable = () -> 
            service.execute(new UpdateProfileCommand(profileId, "Updated", "duplicate@example.com"));
            
        assertThatThrownBy(callable)
            .isInstanceOf(DuplicateEmailException.class)
            .hasMessageContaining("email already exists");
            
        verify(loadProfilePort).findById(profileId);
        verify(saveProfilePort, times(1)).save(any());
    }
}

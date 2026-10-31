package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileCommand;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByEmailPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByIdPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

@ExtendWith(MockitoExtension.class)
class UpdateProfileServiceTest {

    @Mock
    private FindProfileByIdPort findProfileByIdPort;

    @Mock
    private FindProfileByEmailPort findProfileByEmailPort;

    @Mock
    private SaveProfilePort saveProfilePort;

    @InjectMocks
    private UpdateProfileService updateProfileService;

    @Test
    void should_update_profile_successfully() {
        // Arrange
        UUID id = UUID.randomUUID();
        ProfileId profileId = new ProfileId(id);
        String oldName = "Old Name";
        Email oldEmail = new Email("old@example.com");
        Profile existingProfile = Profile.create(profileId, oldName, oldEmail);
        
        String newName = "New Name";
        Email newEmail = new Email("new@example.com");
        UpdateProfileCommand command = new UpdateProfileCommand(profileId, newName, newEmail);

        when(findProfileByIdPort.findById(profileId)).thenReturn(Optional.of(existingProfile));
        when(findProfileByEmailPort.findByEmail(newEmail)).thenReturn(Optional.empty());
        when(saveProfilePort.save(any(Profile.class))).thenReturn(existingProfile);

        // Act
        Profile result = updateProfileService.execute(command);

        // Assert
        assertThat(result).isEqualTo(existingProfile);
        assertThat(result.getName()).isEqualTo(newName);
        assertThat(result.getEmail()).isEqualTo(newEmail);
        verify(findProfileByIdPort).findById(profileId);
        verify(findProfileByEmailPort).findByEmail(newEmail);
        verify(saveProfilePort).save(existingProfile);
    }

    @Test
    void should_throw_profile_not_found_exception_when_updating_non_existent() {
        // Arrange
        UUID id = UUID.randomUUID();
        ProfileId profileId = new ProfileId(id);
        String newName = "New Name";
        Email newEmail = new Email("new@example.com");
        UpdateProfileCommand command = new UpdateProfileCommand(profileId, newName, newEmail);

        when(findProfileByIdPort.findById(profileId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> updateProfileService.execute(command))
                .isInstanceOf(ProfileNotFoundException.class)
                .hasMessageContaining("Profile not found");

        verify(findProfileByEmailPort, never()).findByEmail(any(Email.class));
        verify(saveProfilePort, never()).save(any(Profile.class));
    }

    @Test
    void should_throw_duplicate_email_exception_when_updating_to_existing_email() {
        // Arrange
        UUID id = UUID.randomUUID();
        ProfileId profileId = new ProfileId(id);
        String oldName = "Old Name";
        Email oldEmail = new Email("old@example.com");
        Profile existingProfile = Profile.create(profileId, oldName, oldEmail);
        
        String newName = "New Name";
        Email newEmail = new Email("duplicate@example.com");
        Profile anotherProfile = Profile.create(new ProfileId(UUID.randomUUID()), "Other", newEmail);
        
        UpdateProfileCommand command = new UpdateProfileCommand(profileId, newName, newEmail);

        when(findProfileByIdPort.findById(profileId)).thenReturn(Optional.of(existingProfile));
        when(findProfileByEmailPort.findByEmail(newEmail)).thenReturn(Optional.of(anotherProfile));

        // Act & Assert
        assertThatThrownBy(() -> updateProfileService.execute(command))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("Email already exists");

        verify(saveProfilePort, never()).save(any(Profile.class));
    }
}

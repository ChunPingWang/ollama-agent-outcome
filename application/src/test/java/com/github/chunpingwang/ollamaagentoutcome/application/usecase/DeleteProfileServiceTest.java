package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileCommand;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByIdPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

@ExtendWith(MockitoExtension.class)
class DeleteProfileServiceTest {

    @Mock
    private FindProfileByIdPort findProfileByIdPort;

    @Mock
    private SaveProfilePort saveProfilePort;

    private final DeleteProfileService deleteProfileService = new DeleteProfileService(findProfileByIdPort, saveProfilePort);

    @Test
    void should_delete_profile_successfully() {
        // Arrange
        UUID id = UUID.randomUUID();
        ProfileId profileId = new ProfileId(id);
        String name = "John Doe";
        Email email = new Email("john@example.com");
        Profile existingProfile = Profile.create(profileId, name, email);
        DeleteProfileCommand command = new DeleteProfileCommand(profileId);

        when(findProfileByIdPort.findById(profileId)).thenReturn(Optional.of(existingProfile));
        doNothing().when(saveProfilePort).save(any(Profile.class));

        // Act
        deleteProfileService.execute(command);

        // Assert
        verify(findProfileByIdPort).findById(profileId);
        verify(saveProfilePort).save(existingProfile);
    }

    @Test
    void should_throw_profile_not_found_exception_when_deleting_non_existent() {
        // Arrange
        UUID id = UUID.randomUUID();
        ProfileId profileId = new ProfileId(id);
        DeleteProfileCommand command = new DeleteProfileCommand(profileId);

        when(findProfileByIdPort.findById(profileId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> deleteProfileService.execute(command))
                .isInstanceOf(ProfileNotFoundException.class)
                .hasMessageContaining("Profile not found");

        verify(saveProfilePort, never()).save(any(Profile.class));
    }
}

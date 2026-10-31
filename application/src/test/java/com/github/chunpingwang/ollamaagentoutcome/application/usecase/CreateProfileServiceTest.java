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

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileCommand;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByEmailPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

@ExtendWith(MockitoExtension.class)
class CreateProfileServiceTest {

    @Mock
    private SaveProfilePort saveProfilePort;

    @Mock
    private FindProfileByEmailPort findProfileByEmailPort;

    @InjectMocks
    private CreateProfileService createProfileService;

    @Test
    void should_create_profile_successfully() {
        // Arrange
        UUID id = UUID.randomUUID();
        ProfileId profileId = new ProfileId(id);
        String name = "John Doe";
        Email email = new Email("john@example.com");
        CreateProfileCommand command = new CreateProfileCommand(profileId, name, email);
        Profile expectedProfile = Profile.create(profileId, name, email);

        when(findProfileByEmailPort.findByEmail(email)).thenReturn(Optional.empty());
        when(saveProfilePort.save(any(Profile.class))).thenReturn(expectedProfile);

        // Act
        Profile result = createProfileService.execute(command);

        // Assert
        assertThat(result).isEqualTo(expectedProfile);
        assertThat(result.getId()).isEqualTo(profileId);
        assertThat(result.getName()).isEqualTo(name);
        assertThat(result.getEmail()).isEqualTo(email);
        verify(findProfileByEmailPort).findByEmail(email);
        verify(saveProfilePort).save(any(Profile.class));
    }

    @Test
    void should_throw_duplicate_email_exception_when_email_exists() {
        // Arrange
        UUID id = UUID.randomUUID();
        ProfileId profileId = new ProfileId(id);
        String name = "John Doe";
        Email email = new Email("john@example.com");
        CreateProfileCommand command = new CreateProfileCommand(profileId, name, email);
        Profile existingProfile = Profile.create(profileId, "Existing", email);

        when(findProfileByEmailPort.findByEmail(email)).thenReturn(Optional.of(existingProfile));

        // Act & Assert
        assertThatThrownBy(() -> createProfileService.execute(command))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("Email already exists");

        verify(saveProfilePort, never()).save(any(Profile.class));
    }
}

package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileCommand;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByIdPort;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

@ExtendWith(MockitoExtension.class)
class GetProfileServiceTest {

    @Mock
    private FindProfileByIdPort findProfileByIdPort;

    @InjectMocks
    private GetProfileService getProfileService;

    @Test
    void should_get_profile_successfully() {
        // Arrange
        UUID id = UUID.randomUUID();
        ProfileId profileId = new ProfileId(id);
        String name = "John Doe";
        Email email = new Email("john@example.com");
        Profile expectedProfile = Profile.create(profileId, name, email);
        GetProfileCommand command = new GetProfileCommand(profileId);

        when(findProfileByIdPort.findById(profileId)).thenReturn(Optional.of(expectedProfile));

        // Act
        Profile result = getProfileService.execute(command);

        // Assert
        assertThat(result).isEqualTo(expectedProfile);
        assertThat(result.getId()).isEqualTo(profileId);
        assertThat(result.getName()).isEqualTo(name);
        assertThat(result.getEmail()).isEqualTo(email);
    }

    @Test
    void should_throw_profile_not_found_exception() {
        // Arrange
        UUID id = UUID.randomUUID();
        ProfileId profileId = new ProfileId(id);
        GetProfileCommand command = new GetProfileCommand(profileId);

        when(findProfileByIdPort.findById(profileId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> getProfileService.execute(command))
                .isInstanceOf(ProfileNotFoundException.class)
                .hasMessageContaining("Profile not found");
    }
}

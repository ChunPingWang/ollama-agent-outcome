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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfileByIdPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByIdPort;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Age;

@ExtendWith(MockitoExtension.class)
class DeleteProfileServiceTest {

    @Mock
    private FindProfileByIdPort findProfileByIdPort;

    @Mock
    private DeleteProfileByIdPort deleteProfileByIdPort;

    @InjectMocks
    private DeleteProfileService deleteProfileService;

    @Test
    void should_delete_profile_successfully() {
        // Given
        UUID uuid = UUID.randomUUID();
        ProfileId profileId = new ProfileId(uuid);
        Profile profile = new Profile(
                profileId,
                new Email("user@example.com"),
                new Name("John Doe"),
                new Age(25)
        );

        when(findProfileByIdPort.findById(profileId)).thenReturn(Optional.of(profile));
        doNothing().when(deleteProfileByIdPort).deleteById(profileId);

        // When
        deleteProfileService.deleteProfile(profileId);

        // Then
        verify(deleteProfileByIdPort).deleteById(profileId);
    }

    @Test
    void should_throw_exception_when_profile_not_found() {
        // Given
        UUID uuid = UUID.randomUUID();
        ProfileId profileId = new ProfileId(uuid);

        when(findProfileByIdPort.findById(profileId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> deleteProfileService.deleteProfile(profileId))
                .isInstanceOf(ProfileNotFoundException.class)
                .hasMessageContaining("Profile not found");

        verify(deleteProfileByIdPort, never()).deleteById(any());
    }
}

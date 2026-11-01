package com.github.chunpingwang.ollamaagentoutcome.application.usecase;



import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfileByIdPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByIdPort;

@MockitoSettings(strictness = Strictness.LENIENT)
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
        UUID id = UUID.randomUUID();
        ProfileId profileId = new ProfileId(id);
        
        Profile existingProfile = Profile.create(id, "delete@example.com", "Delete Me");
        when(findProfileByIdPort.findById(profileId)).thenReturn(Optional.of(existingProfile));
        doNothing().when(deleteProfileByIdPort).deleteById(profileId);

        // When
        deleteProfileService.execute(profileId);

        // Then
        verify(findProfileByIdPort).findById(profileId);
        verify(deleteProfileByIdPort).deleteById(profileId);
    }

    @Test
    void should_throw_exception_when_not_found_for_delete() {
        // Given
        UUID id = UUID.randomUUID();
        ProfileId profileId = new ProfileId(id);
        
        when(findProfileByIdPort.findById(profileId)).thenReturn(Optional.empty());

        // When & Then
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> 
            deleteProfileService.execute(profileId)
        ).isInstanceOf(ProfileNotFoundException.class)
         .hasMessageContaining("not found");
         
        verify(findProfileByIdPort).findById(profileId);
        verify(deleteProfileByIdPort, org.mockito.Mockito.never()).deleteById(any());
    }
}

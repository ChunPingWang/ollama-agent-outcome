package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.exception.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeleteProfileServiceTest {

    @Mock
    private LoadProfilePort loadProfilePort;

    @Mock
    private DeleteProfilePort deleteProfilePort;

    @InjectMocks
    private DeleteProfileService deleteProfileService;

    @Test
    void should_delete_profile_successfully() {
        // Given
        UUID id = UUID.randomUUID();
        ProfileId profileId = new ProfileId(id);
        
        when(loadProfilePort.findById(profileId)).thenReturn(Optional.of(new Profile(profileId, "John", new Email("john@example.com"))));

        // When
        deleteProfileService.delete(id);

        // Then
        verify(deleteProfilePort).deleteById(profileId);
    }

    @Test
    void should_throw_exception_when_profile_not_found() {
        // Given
        UUID id = UUID.randomUUID();
        ProfileId profileId = new ProfileId(id);
        
        when(loadProfilePort.findById(profileId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> deleteProfileService.delete(id))
                .isInstanceOf(ProfileNotFoundException.class)
                .hasMessageContaining(id.toString());
    }
}

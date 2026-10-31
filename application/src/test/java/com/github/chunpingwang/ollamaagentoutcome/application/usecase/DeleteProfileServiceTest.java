package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.Bio;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.service.ProfileNotFoundException;

@ExtendWith(MockitoExtension.class)
class DeleteProfileServiceTest {

    @Mock
    private LoadProfilePort loadProfilePort;

    @InjectMocks
    private DeleteProfileService deleteProfileService;

    @Test
    void should_delete_profile_successfully() {
        // Given
        UUID id = UUID.randomUUID();
        Profile existingProfile = new Profile(
            new ProfileId(id),
            new Email("test@example.com"),
            new DisplayName("Tester"),
            new Bio("Bio")
        );
        
        when(loadProfilePort.findById(id)).thenReturn(Optional.of(existingProfile));
        doNothing().when(loadProfilePort).deleteById(id);

        // When & Then
        assertThatCode(() -> deleteProfileService.execute(id))
                .doesNotThrowAnyException();

        verify(loadProfilePort).findById(id);
        verify(loadProfilePort).deleteById(id);
    }

    @Test
    void should_throw_exception_when_profile_not_found_for_deletion() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(loadProfilePort.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> deleteProfileService.execute(nonExistentId))
                .isInstanceOf(ProfileNotFoundException.class)
                .hasMessageContaining("profile not found with id: " + nonExistentId);
    }
}

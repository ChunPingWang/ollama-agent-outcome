package com.github.chunpingwang.ollamaagentoutcome.application.usecase;




import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;

/**
 * DeleteProfileService 的單元測試。
 */
@MockitoSettings(strictness = Strictness.LENIENT)
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
        // Arrange
        UUID id = UUID.randomUUID();
        Profile profile = new Profile(
            new ProfileId(id), 
            new Email("user@example.com"), 
            new DisplayName("User")
        );
        
        when(loadProfilePort.findById(new ProfileId(id))).thenReturn(Optional.of(profile));
        doNothing().when(deleteProfilePort).deleteById(any(ProfileId.class));

        // Act & Assert
        deleteProfileService.execute(new ProfileId(id));
        
        verify(loadProfilePort).findById(new ProfileId(id));
        verify(deleteProfilePort).deleteById(new ProfileId(id));
    }

    @Test
    void should_throw_exception_when_profile_not_found() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(loadProfilePort.findById(new ProfileId(id))).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> deleteProfileService.execute(new ProfileId(id)))
                .isInstanceOf(ProfileNotFoundException.class)
                .hasMessageContaining(id.toString());
                
        verifyNoInteractions(deleteProfilePort);
    }
}

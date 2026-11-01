package com.github.chunpingwang.ollamaagentoutcome.application.usecase;



import org.mockito.junit.jupiter.MockitoSettings;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class DeleteProfileServiceTest {

    @Mock
    private LoadProfilePort loadProfilePort;

    @Mock
    private DeleteProfilePort deleteProfilePort;

    private DeleteProfileUseCase deleteProfileService;

    @BeforeEach
    void setUp() {
        deleteProfileService = new DeleteProfileService(loadProfilePort, deleteProfilePort);
    }

    @Test
    void should_delete_profile_when_exists() {
        String idStr = "550e8400-e29b-41d4-a716-446655440000";
        
        var existingProfile = Profile.create(
            new ProfileId(idStr), 
            new Email("delete@example.com"), 
            new DisplayName("Delete User")
        );
        
        when(loadProfilePort.findById(idStr)).thenReturn(Optional.of(existingProfile));
        
        deleteProfileService.execute(idStr);
        
        verify(loadProfilePort, times(1)).findById(idStr);
        verify(deleteProfilePort, times(1)).deleteById(idStr);
    }

    @Test
    void should_throw_exception_when_not_found_for_deletion() {
        String notFoundId = "non-existent-delete-id";
        
        when(loadProfilePort.findById(notFoundId)).thenReturn(Optional.empty());
        
        assertThatThrownBy(() -> deleteProfileService.execute(notFoundId))
            .isInstanceOf(ProfileNotFoundException.class)
            .hasMessageContaining(notFoundId);
            
        verify(loadProfilePort, times(1)).findById(notFoundId);
        verify(deleteProfilePort, never()).deleteById(anyString());
    }
}

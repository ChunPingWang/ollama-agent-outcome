package com.github.chunpingwang.ollamaagentoutcome.application.usecase;



import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    void should_delete_profile_when_found() {
        String idStr = "550e8400-e29b-41d4-a716-446655440000";
        
        when(loadProfilePort.findById(idStr)).thenReturn(Optional.of(createSampleProfile(idStr)));
        doNothing().when(deleteProfilePort).deleteById(anyString());

        deleteProfileService.deleteById(idStr);

        verify(loadProfilePort).findById(idStr);
        verify(deleteProfilePort).deleteById(idStr);
    }

    @Test
    void should_throw_exception_when_not_found_for_deletion() {
        String notFoundId = "non-existent-id";
        
        when(loadProfilePort.findById(notFoundId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> deleteProfileService.deleteById(notFoundId))
            .isInstanceOf(ProfileNotFoundException.class)
            .hasMessageContaining(notFoundId);
            
        verify(loadProfilePort).findById(notFoundId);
        verify(deleteProfilePort, org.mockito.Mockito.never()).deleteById(anyString());
    }
    
    private Profile createSampleProfile(String id) {
        return Profile.create(
            new ProfileId(id), 
            new Email("test@example.com"), 
            new DisplayName("Tester")
        );
    }
}

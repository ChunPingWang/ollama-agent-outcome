package com.github.chunpingwang.ollamaagentoutcome.application.usecase;



import org.mockito.junit.jupiter.MockitoSettings;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
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
class UpdateProfileServiceTest {

    @Mock
    private LoadProfilePort loadProfilePort;

    @Mock
    private SaveProfilePort saveProfilePort;

    private UpdateProfileUseCase updateProfileService;

    @BeforeEach
    void setUp() {
        updateProfileService = new UpdateProfileService(loadProfilePort, saveProfilePort);
    }

    @Test
    void should_load_update_and_save_profile() {
        String idStr = "550e8400-e29b-41d4-a716-446655440000";
        var existingEmail = new Email("old@example.com");
        var oldDisplayName = new DisplayName("Old Name");
        
        Profile existingProfile = Profile.create(
            new ProfileId(idStr), 
            existingEmail, 
            oldDisplayName
        );
        
        when(loadProfilePort.findById(idStr)).thenReturn(Optional.of(existingProfile));
        
        var request = new UpdateProfileUseCase.UpdateProfileRequest("new@example.com", "New Name");
        
        updateProfileService.execute(request, idStr);
        
        verify(loadProfilePort, times(1)).findById(idStr);
        
        assertThat(existingProfile.email().address()).isEqualTo("new@example.com");
        assertThat(existingProfile.displayName().name()).isEqualTo("New Name");
        
        verify(saveProfilePort, times(1)).save(existingProfile);
    }

    @Test
    void should_throw_exception_when_profile_not_found_for_update() {
        String notFoundId = "missing-id";
        
        when(loadProfilePort.findById(notFoundId)).thenReturn(Optional.empty());
        
        var request = new UpdateProfileUseCase.UpdateProfileRequest("test@test.com", "Name");
        
        assertThatThrownBy(() -> updateProfileService.execute(request, notFoundId))
            .isInstanceOf(ProfileNotFoundException.class)
            .hasMessageContaining(notFoundId);
            
        verify(loadProfilePort, times(1)).findById(notFoundId);
        verify(saveProfilePort, never()).save(any());
    }
}

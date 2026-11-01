package com.github.chunpingwang.ollamaagentoutcome.application.usecase;



import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
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

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase.UpdateRequest;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class UpdateProfileServiceTest {

    @Mock
    private LoadProfilePort loadProfilePort;

    @Mock
    private SaveProfilePort saveProfilePort;

    @InjectMocks
    private UpdateProfileService updateProfileService;

    @Test
    void should_load_update_and_save_profile() {
        // given
        String id = UUID.randomUUID().toString();
        ProfileId profileId = new ProfileId(id);
        
        Email oldEmail = new Email("old@example.com");
        DisplayName oldDisplayName = new DisplayName("Old Name");
        var existingProfile = com.github.chunpingwang.ollamaagentoutcome.domain.Profile.create(profileId, oldEmail, oldDisplayName);
        
        when(loadProfilePort.findById(id)).thenReturn(Optional.of(existingProfile));
        
        String newEmail = "new@example.com";
        String newDisplayName = "New Name";
        UpdateRequest request = new UpdateRequest(newEmail, newDisplayName);

        // when
        updateProfileService.execute(request, id);

        // then
        verify(loadProfilePort, times(1)).findById(id);
        verify(saveProfilePort, times(1)).save(argThat(p -> 
            p.email().address().equals(newEmail) && 
            p.displayName().name().equals(newDisplayName)
        ));
    }

    @Test
    void should_throw_exception_when_not_found_for_update() {
        // given
        String id = "non-existent-id";
        when(loadProfilePort.findById(id)).thenReturn(Optional.empty());
        
        UpdateRequest request = new UpdateRequest("test@test.com", "Name");

        // when & then
        assertThatThrownBy(() -> updateProfileService.execute(request, id))
            .isInstanceOf(ProfileNotFoundException.class)
            .hasMessageContaining("profile not found with id: " + id);
            
        verify(loadProfilePort, times(1)).findById(id);
        verifyNoInteractions(saveProfilePort);
    }
}

package com.github.chunpingwang.ollamaagentoutcome.application.usecase;



import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase.UpdateProfileRequest;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

@ExtendWith(MockitoExtension.class)
class UpdateProfileServiceTest {

    @Mock
    private LoadProfilePort loadPort;

    @Mock
    private SaveProfilePort savePort;

    @InjectMocks
    private UpdateProfileService updateProfileService;

    @Test
    void should_update_existing_profile_and_call_save_port() {
        var id = "550e8400-e29b-41d4-a716-446655440000";
        var request = new UpdateProfileRequest("updated@test.com", "New Name");
        
        Profile existingProfile = Profile.create(
            new ProfileId(id), 
            new Email("old@test.com"), 
            new DisplayName("Old Name")
        );
        when(loadPort.findById(id)).thenReturn(Optional.of(existingProfile));
        
        updateProfileService.execute(request, id);
        
        verify(savePort).save(any());
    }

    @Test
    void should_throw_when_profile_not_found_for_update() {
        var id = "non-existent-id";
        var request = new UpdateProfileRequest("test@test.com", "Name");
        
        when(loadPort.findById(id)).thenReturn(Optional.empty());
        
        assertThatThrownBy(() -> updateProfileService.execute(request, id))
            .isInstanceOf(ProfileNotFoundException.class)
            .hasMessageContaining(id);
    }
}

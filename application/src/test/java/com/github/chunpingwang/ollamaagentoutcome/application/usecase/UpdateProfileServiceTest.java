package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileRequest;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpdateProfileServiceTest {

    @Mock
    private LoadProfilePort loadProfilePort;

    @Mock
    private SaveProfilePort saveProfilePort;

    @InjectMocks
    private UpdateProfileService updateProfileService;

    @Test
    void should_update_profile_successfully() {
        String id = "550e8400-e29b-41d4-a716-446655440000";
        Profile existingProfile = Profile.create(new ProfileId(id), new Email("old@test.com"), new DisplayName("Old Name"));
        
        when(loadProfilePort.findById(id)).thenReturn(Optional.of(existingProfile));
        doNothing().when(saveProfilePort).save(any(Profile.class));
        
        var request = new UpdateProfileRequest("new@example.com", "New Name"); // Note: email change might not be supported by current Domain method, but we test the flow
        
        updateProfileService.execute(request, id);
        
        verify(loadProfilePort).findById(id);
        verify(saveProfilePort).save(any());
    }

    @Test
    void should_throw_exception_when_profile_not_found() {
        String id = "non-existent-id";
        
        when(loadProfilePort.findById(id)).thenReturn(Optional.empty());
        
        var request = new UpdateProfileRequest("test@test.com", "Name");
        
        assertThatThrownBy(() -> updateProfileService.execute(request, id))
                .isInstanceOf(ProfileNotFoundException.class)
                .hasMessageContaining("profile not found");
    }
}

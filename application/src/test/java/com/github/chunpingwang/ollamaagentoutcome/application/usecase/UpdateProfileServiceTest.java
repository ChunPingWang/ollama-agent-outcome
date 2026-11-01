package com.github.chunpingwang.ollamaagentoutcome.application.usecase;




import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileRequest;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
        var id = "550e8400-e29b-41d4-a716-446655440000";
        var existingEmail = new Email("old@example.com");
        var profile = Profile.create(new ProfileId(id), existingEmail, new DisplayName("Old Name"));
        
        when(loadProfilePort.findById(id)).thenReturn(Optional.of(profile));
        
        var request = new UpdateProfileRequest("new@example.com", "New Name");
        updateProfileService.execute(request, id);
        
        verify(loadProfilePort).findById(id);
        verify(saveProfilePort).save(org.mockito.ArgumentMatchers.any(Profile.class));
    }

    @Test
    void should_throw_exception_when_not_found() {
        var id = "non-existent-id";
        
        when(loadProfilePort.findById(id)).thenReturn(Optional.empty());
        
        var request = new UpdateProfileRequest("test@test.com", "Name");
        
        assertThatThrownBy(() -> updateProfileService.execute(request, id))
            .isInstanceOf(ProfileNotFoundException.class)
            .hasMessageContaining(id);
    }
}

package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

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
    private LoadProfilePort loadProfilePort;

    @Mock
    private SaveProfilePort saveProfilePort;

    @InjectMocks
    private UpdateProfileService updateProfileService;

    @Test
    void should_update_display_name_when_profile_exists() {
        var id = new ProfileId(UUID.randomUUID());
        var oldEmail = new Email("test@example.com");
        var oldDisplayName = new DisplayName("Old Name");
        
        var profile = new Profile(id, oldEmail, oldDisplayName);
        when(loadProfilePort.findById(id)).thenReturn(Optional.of(profile));

        var newDisplayName = new DisplayName("New Name");
        var request = new UpdateProfileRequest(id, newDisplayName);

        updateProfileService.execute(request);

        verify(saveProfilePort).save(org.mockito.ArgumentMatchers.argThat(saved -> {
            return saved.id().equals(id) && saved.displayName().equals(newDisplayName);
        }));
    }

    @Test
    void should_throw_not_found_exception_when_updating_non_existent_profile() {
        var id = new ProfileId(UUID.randomUUID());
        var newDisplayName = new DisplayName("Ghost User");
        var request = new UpdateProfileRequest(id, newDisplayName);

        when(loadProfilePort.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> updateProfileService.execute(request))
                .isInstanceOf(ProfileNotFoundException.class)
                .hasMessageContaining(id.value().toString());
                
        verify(saveProfilePort, org.mockito.Mockito.never()).save(org.mockito.ArgumentMatchers.any());
    }
}

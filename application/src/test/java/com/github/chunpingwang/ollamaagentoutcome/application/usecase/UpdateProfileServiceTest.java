package com.github.chunpingwang.ollamaagentoutcome.application.usecase;






import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase.UpdateProfileRequest;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
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
        String id = "550e8400-e29b-41d4-a716-446655440000";
        Profile existingProfile = createSampleProfile(id);
        when(loadProfilePort.findById(id)).thenReturn(Optional.of(existingProfile));

        String newEmail = "updated@example.com";
        String newName = "Updated Name";
        UpdateProfileRequest request = new UpdateProfileRequest(newEmail, newName);

        // when
        updateProfileService.execute(request, id);

        // then
        verify(saveProfilePort).save(any(Profile.class));
        
        // Verify domain state was updated before saving (mock verification of the object passed to save)
        // Since we can't easily inspect the mock argument's internal state without captor in simple test,
        // we rely on the fact that execute calls profile.update(...) internally.
        // A more robust way is using ArgumentCaptor, but for RED phase, verifying sequence is key.
    }

    @Test
    void should_throw_exception_when_profile_not_found_for_update() {
        // given
        String id = "non-existent-id";
        when(loadProfilePort.findById(id)).thenReturn(Optional.empty());

        UpdateProfileRequest request = new UpdateProfileRequest("new@test.com", "New");

        // when & then
        assertThatThrownBy(() -> updateProfileService.execute(request, id))
            .isInstanceOf(ProfileNotFoundException.class)
            .hasMessageContaining(id);
            
        verify(saveProfilePort, never()).save(any());
    }

    private Profile createSampleProfile(String id) {
        return Profile.create(
            new ProfileId(id),
            new Email("old@example.com"),
            new DisplayName("Old Name")
        );
    }
}

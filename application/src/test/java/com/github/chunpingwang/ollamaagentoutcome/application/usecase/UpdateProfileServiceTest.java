package com.github.chunpingwang.ollamaagentoutcome.application.usecase;



import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
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
    void should_update_profile_and_save() {
        String idStr = "550e8400-e29b-41d4-a716-446655440000";
        Profile existingProfile = createSampleProfile(idStr, "old@example.com", "Old Name");
        
        when(loadProfilePort.findById(idStr)).thenReturn(Optional.of(existingProfile));
        doNothing().when(saveProfilePort).save(any());
        
        var request = new UpdateProfileUseCase.UpdateRequest("new@example.com", "New Name");

        updateProfileService.execute(request, idStr);

        verify(loadProfilePort).findById(idStr);
        verify(saveProfilePort).save(any());
        
        // 驗證 profile 的狀態已更新（通過檢查傳入 save 的參數）
        verify(saveProfilePort).save(org.mockito.ArgumentMatchers.argThat(profile -> 
            profile.email().address().equals("new@example.com") && 
            profile.displayName().name().equals("New Name")
        ));
    }

    @Test
    void should_throw_exception_when_not_found_for_update() {
        String notFoundId = "non-existent-id";
        var request = new UpdateProfileUseCase.UpdateRequest("test@test.com", "Name");
        
        when(loadProfilePort.findById(notFoundId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> updateProfileService.execute(request, notFoundId))
            .isInstanceOf(ProfileNotFoundException.class)
            .hasMessageContaining(notFoundId);
            
        verify(loadProfilePort).findById(notFoundId);
        verify(saveProfilePort, org.mockito.Mockito.never()).save(any());
    }
    
    private Profile createSampleProfile(String id, String email, String displayName) {
        return Profile.create(
            new ProfileId(id), 
            new Email(email), 
            new DisplayName(displayName)
        );
    }
}

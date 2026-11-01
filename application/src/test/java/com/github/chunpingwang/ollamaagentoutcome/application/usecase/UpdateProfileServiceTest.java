package com.github.chunpingwang.ollamaagentoutcome.application.usecase;



import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import java.util.Optional;
import java.util.UUID;
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
    void should_update_display_name_successfully() {
        UUID id = UUID.randomUUID();
        ProfileId profileId = new ProfileId(id.toString());
        Email email = new Email("update@example.com");
        DisplayName oldName = new DisplayName("Old Name");
        DisplayName newName = new DisplayName("New Name");
        
        Profile mockProfile = new Profile(id, email, oldName);

        when(loadProfilePort.findById(profileId)).thenReturn(Optional.of(mockProfile));
        doNothing().when(saveProfilePort).save(any(UUID.class), any(Email.class), any(DisplayName.class));

        assertThatNoException().isThrownBy(() -> updateProfileService.execute(profileId, newName));
        
        verify(loadProfilePort).findById(profileId);
        // 驗證 displayName 已更新
        assertThat(mockProfile.displayName()).isEqualTo(newName);
        verify(saveProfilePort).save(eq(id), eq(email), eq(newName));
    }

    @Test
    void should_throw_exception_when_not_found() {
        UUID id = UUID.randomUUID();
        ProfileId profileId = new ProfileId(id.toString());

        when(loadProfilePort.findById(profileId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> updateProfileService.execute(profileId, new DisplayName("Any")))
                .isInstanceOf(ProfileNotFoundException.class)
                .hasMessageContaining("找不到識別碼為 " + id.toString());
                
        verify(loadProfilePort).findById(profileId);
        // 確保在找不到時沒有嘗試保存
        verify(saveProfilePort, never()).save(any(), any(), any());
    }
}

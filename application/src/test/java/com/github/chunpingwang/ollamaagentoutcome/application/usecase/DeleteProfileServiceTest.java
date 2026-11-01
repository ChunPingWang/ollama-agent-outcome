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
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.RemoveProfilePort;
import java.util.Optional;
import java.util.UUID;
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
    private RemoveProfilePort removeProfilePort;

    @InjectMocks
    private DeleteProfileService deleteProfileService;

    @Test
    void should_delete_profile_successfully() {
        UUID id = UUID.randomUUID();
        ProfileId profileId = new ProfileId(id.toString());
        
        // 建立一個有效的 Profile 物件，因為 findById 回傳 Optional<Profile>
        Email email = new Email("delete@example.com");
        DisplayName displayName = new DisplayName("Delete User");
        Profile mockProfile = new Profile(id, email, displayName);

        when(loadProfilePort.findById(profileId)).thenReturn(Optional.of(mockProfile));
        
        doNothing().when(removeProfilePort).deleteById(any(ProfileId.class));

        assertThatNoException().isThrownBy(() -> deleteProfileService.execute(profileId));
        
        verify(loadProfilePort).findById(profileId);
        verify(removeProfilePort).deleteById(profileId);
    }

    @Test
    void should_throw_exception_when_not_found() {
        UUID id = UUID.randomUUID();
        ProfileId profileId = new ProfileId(id.toString());

        when(loadProfilePort.findById(profileId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> deleteProfileService.execute(profileId))
                .isInstanceOf(ProfileNotFoundException.class)
                .hasMessageContaining("找不到識別碼為 " + id.toString());
                
        verify(loadProfilePort).findById(profileId);
        // 確保在找不到時沒有嘗試刪除
        verify(removeProfilePort, never()).deleteById(any());
    }
}

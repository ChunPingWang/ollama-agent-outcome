package com.github.chunpingwang.ollamaagentoutcome.application.usecase;


import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
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
    private DeleteProfilePort deleteProfilePort;

    @InjectMocks
    private DeleteProfileService deleteProfileService;

    private final UUID testId = UUID.randomUUID();
    private final ProfileId profileId = new ProfileId(testId);

    @Test
    void should_call_delete_when_profile_exists() {
        // 假設 port 回傳了現有的個人資料
        Email email = new Email("user@example.com");
        DisplayName displayName = new DisplayName("Display Name");
        Profile existingProfile = new Profile(profileId, email, displayName);
        
        when(loadProfilePort.findById(profileId)).thenReturn(Optional.of(existingProfile));
        doNothing().when(deleteProfilePort).deleteById(any(ProfileId.class));

        // 當呼叫刪除時，應先載入確認存在，再執行刪除
        deleteProfileService.execute(testId);

        verify(loadProfilePort, times(1)).findById(profileId);
        verify(deleteProfilePort, times(1)).deleteById(profileId);
    }

    @Test
    void should_throw_profile_not_found_when_profile_does_not_exist() {
        // 假設 port 找不到資料
        when(loadProfilePort.findById(profileId)).thenReturn(Optional.empty());

        // 那麼應該拋出 ProfileNotFoundException
        assertThatThrownBy(() -> deleteProfileService.execute(testId))
                .isInstanceOf(ProfileNotFoundException.class)
                .hasMessageContaining(testId.toString());
        
        verifyNoInteractions(deleteProfilePort);
    }
}

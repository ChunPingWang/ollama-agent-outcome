package com.github.chunpingwang.ollamaagentoutcome.application.usecase;



import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
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
class GetProfileServiceTest {

    @Mock
    private LoadProfilePort loadProfilePort;

    @InjectMocks
    private GetProfileService getProfileService;

    private final UUID testId = UUID.randomUUID();
    private final ProfileId profileId = new ProfileId(testId);

    @Test
    void should_return_optional_when_profile_exists() {
        // 假設 port 回傳了資料
        Email email = new Email("user@example.com");
        DisplayName displayName = new DisplayName("Display Name");
        Profile existingProfile = new Profile(profileId, email, displayName);
        
        when(loadProfilePort.findById(profileId)).thenReturn(Optional.of(existingProfile));

        Optional<Profile> result = getProfileService.findById(profileId);

        assertThat(result).isPresent().containsSame(existingProfile);
        verify(loadProfilePort, times(1)).findById(profileId);
    }

    @Test
    void should_return_empty_optional_when_profile_not_found() {
        // 假設 port 找不到資料
        when(loadProfilePort.findById(profileId)).thenReturn(Optional.empty());

        Optional<Profile> result = getProfileService.findById(profileId);

        assertThat(result).isEmpty();
        verify(loadProfilePort, times(1)).findById(profileId);
    }
}

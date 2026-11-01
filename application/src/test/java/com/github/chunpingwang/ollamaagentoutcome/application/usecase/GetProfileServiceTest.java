package com.github.chunpingwang.ollamaagentoutcome.application.usecase;




import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
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

    @Test
    void should_return_profile_result_when_found() {
        UUID id = UUID.randomUUID();
        ProfileId profileId = new ProfileId(id.toString());
        Email email = new Email("found@example.com");
        DisplayName displayName = new DisplayName("Found User");
        
        Profile mockProfile = new Profile(id, email, displayName);

        when(loadProfilePort.findById(profileId)).thenReturn(Optional.of(mockProfile));

        GetProfileUseCase.ProfileResult result = getProfileService.execute(profileId);

        assertThat(result).isNotNull();
        assertThat(result.email()).isEqualTo("found@example.com");
        assertThat(result.displayName()).isEqualTo("Found User");
        
        verify(loadProfilePort).findById(profileId);
    }

    @Test
    void should_throw_exception_when_not_found() {
        UUID id = UUID.randomUUID();
        ProfileId profileId = new ProfileId(id.toString());

        when(loadProfilePort.findById(profileId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> getProfileService.execute(profileId))
                .isInstanceOf(ProfileNotFoundException.class)
                .hasMessageContaining("找不到識別碼為 " + id.toString());
                
        verify(loadProfilePort).findById(profileId);
    }
}

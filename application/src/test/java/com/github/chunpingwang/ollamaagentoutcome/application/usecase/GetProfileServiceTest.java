package com.github.chunpingwang.ollamaagentoutcome.application.usecase;



import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
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
    void should_return_profile_when_found() {
        UUID id = UUID.randomUUID();
        ProfileId profileId = new ProfileId(id);
        Email email = new Email("test@example.com");
        DisplayName displayName = new DisplayName("User");
        Profile expectedProfile = new Profile(profileId, email, displayName);

        when(loadProfilePort.findById(profileId)).thenReturn(Optional.of(expectedProfile));

        Optional<Profile> result = getProfileService.execute(profileId);

        assertThat(result).isPresent().get().isEqualTo(expectedProfile);
    }

    @Test
    void should_return_empty_optional_when_not_found() {
        UUID id = UUID.randomUUID();
        ProfileId profileId = new ProfileId(id);

        when(loadProfilePort.findById(profileId)).thenReturn(Optional.empty());

        Optional<Profile> result = getProfileService.execute(profileId);

        assertThat(result).isEmpty();
    }
}

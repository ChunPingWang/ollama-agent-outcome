package com.github.chunpingwang.ollamaagentoutcome.application.usecase;



import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
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
        UUID idVal = UUID.randomUUID();
        ProfileId profileId = new ProfileId(idVal);
        
        Email email = new Email("found@example.com");
        DisplayName displayName = new DisplayName("Found User");
        Profile expectedProfile = new Profile(profileId, email, displayName);
        
        when(loadProfilePort.findById(profileId)).thenReturn(Optional.of(expectedProfile));

        Optional<Profile> result = getProfileService.execute(profileId);

        assertThat(result).isPresent().get().isEqualTo(expectedProfile);
        verify(loadProfilePort).findById(profileId);
    }

    @Test
    void should_return_empty_optional_when_not_found() {
        UUID idVal = UUID.randomUUID();
        ProfileId profileId = new ProfileId(idVal);
        
        when(loadProfilePort.findById(profileId)).thenReturn(Optional.empty());

        Optional<Profile> result = getProfileService.execute(profileId);

        assertThat(result).isEmpty();
        verify(loadProfilePort).findById(profileId);
    }
}

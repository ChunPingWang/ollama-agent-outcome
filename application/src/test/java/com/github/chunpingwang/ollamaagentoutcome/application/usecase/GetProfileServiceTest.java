package com.github.chunpingwang.ollamaagentoutcome.application.usecase;



import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByIdPort;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetProfileServiceTest {

    @Mock
    private FindProfileByIdPort findProfileByIdPort;

    @InjectMocks
    private GetProfileService getProfileService;

    @Test
    void should_return_profile_when_exists() {
        UUID id = UUID.randomUUID();
        String emailAddr = "user@example.com";
        String name = "User Name";
        
        Profile expectedProfile = new Profile(
            new ProfileId(id), 
            new Email(emailAddr), 
            new DisplayName(name)
        );

        when(findProfileByIdPort.findById(id)).thenReturn(Optional.of(expectedProfile));

        Optional<Profile> result = getProfileService.findById(id);

        assertThat(result).isPresent();
        Profile profile = result.get();
        assertThat(profile.getId().getValue()).isEqualTo(id);
        assertThat(profile.getEmail().getAddress()).isEqualTo(emailAddr);
        assertThat(profile.getDisplayName().getName()).isEqualTo(name);
    }

    @Test
    void should_return_empty_when_missing() {
        UUID missingId = UUID.randomUUID();

        when(findProfileByIdPort.findById(missingId)).thenReturn(Optional.empty());

        Optional<Profile> result = getProfileService.findById(missingId);

        assertThat(result).isEmpty();
    }
}

package com.github.chunpingwang.ollamaagentoutcome.application.usecase;



import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByIdPort;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class GetProfileServiceTest {

    @Mock
    private FindProfileByIdPort findProfileByIdPort;

    @InjectMocks
    private GetProfileService getProfileService;

    @Test
    void should_return_profile_when_found() {
        UUID id = UUID.randomUUID();
        ProfileId profileId = new ProfileId(id.toString());
        Email email = new Email("user@example.com");
        DisplayName displayName = new DisplayName("John Doe");
        
        Profile expectedProfile = new Profile(id, email, displayName);
        when(findProfileByIdPort.findById(profileId)).thenReturn(Optional.of(expectedProfile));
        
        Profile result = getProfileService.execute(profileId);
        
        assertThat(result).isEqualTo(expectedProfile);
        assertThat(result.id()).isEqualTo(profileId);
    }

    @Test
    void should_throw_not_found_exception_when_profile_does_not_exist() {
        UUID id = UUID.randomUUID();
        ProfileId profileId = new ProfileId(id.toString());
        
        when(findProfileByIdPort.findById(profileId)).thenReturn(Optional.empty());
        
        assertThatThrownBy(() -> getProfileService.execute(profileId))
            .isInstanceOf(ProfileNotFoundException.class)
            .hasMessageContaining(id.toString());
    }
}

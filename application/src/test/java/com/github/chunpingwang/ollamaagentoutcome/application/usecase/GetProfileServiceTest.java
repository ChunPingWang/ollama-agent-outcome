package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;

@ExtendWith(MockitoExtension.class)
class GetProfileServiceTest {

    private final UUID testId = UUID.randomUUID();
    
    @Mock
    private LoadProfilePort loadProfilePort;

    @InjectMocks
    private GetProfileService getProfileService;

    @Test
    void should_return_profile_when_found() {
        Profile profile = new Profile(new ProfileId(testId), 
                new com.github.chunpingwang.ollamaagentoutcome.domain.Email("test@example.com"), 
                new com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName("User"));
        
        when(loadProfilePort.findById(new ProfileId(testId))).thenReturn(Optional.of(profile));

        Optional<Profile> result = getProfileService.execute(new ProfileId(testId));

        assertThat(result).isPresent().containsSame(profile);
    }

    @Test
    void should_return_empty_optional_when_not_found() {
        when(loadProfilePort.findById(new ProfileId(testId))).thenReturn(Optional.empty());

        Optional<Profile> result = getProfileService.execute(new ProfileId(testId));

        assertThat(result).isEmpty();
    }
}

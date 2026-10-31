package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetProfileServiceTest {

    @Mock
    private LoadProfilePort loadProfilePort;

    @InjectMocks
    private GetProfileService getProfileService;

    @Test
    void should_return_profile_when_found() {
        String id = "550e8400-e29b-41d4-a716-446655440000";
        Profile profile = Profile.create(new ProfileId(id), new Email("test@example.com"), new DisplayName("User"));
        
        when(loadProfilePort.findById(id)).thenReturn(Optional.of(profile));
        
        Optional<Profile> result = getProfileService.execute(id);
        
        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo(new ProfileId(id));
    }

    @Test
    void should_return_empty_when_not_found() {
        String id = "non-existent-id";
        
        when(loadProfilePort.findById(id)).thenReturn(Optional.empty());
        
        var result = getProfileService.execute(id);
        
        assertThat(result).isEmpty();
    }
}

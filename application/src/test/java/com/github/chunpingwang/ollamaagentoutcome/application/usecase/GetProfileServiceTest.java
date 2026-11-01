package com.github.chunpingwang.ollamaagentoutcome.application.usecase;



import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

@ExtendWith(MockitoExtension.class)
class GetProfileServiceTest {

    @Mock
    private LoadProfilePort loadPort;

    @InjectMocks
    private GetProfileService getProfileService;

    @Test
    void should_return_profile_when_found() {
        var id = "550e8400-e29b-41d4-a716-446655440000";
        
        Profile profile = Profile.create(
            new ProfileId(id), 
            new Email("query@example.com"), 
            new DisplayName("Query User")
        );
        when(loadPort.findById(id)).thenReturn(Optional.of(profile));
        
        Optional<Profile> result = getProfileService.execute(id);
        
        assertThat(result).isPresent();
        assertThat(result.get().email()).isEqualTo(new Email("query@example.com"));
    }

    @Test
    void should_return_empty_when_not_found() {
        var id = "non-existent-id";
        
        when(loadPort.findById(id)).thenReturn(Optional.empty());
        
        assertThat(getProfileService.execute(id)).isEmpty();
    }
}

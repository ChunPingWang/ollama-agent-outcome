package com.github.chunpingwang.ollamaagentoutcome.application.usecase;



import org.mockito.junit.jupiter.MockitoSettings;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class GetProfileServiceTest {

    @Mock
    private LoadProfilePort loadProfilePort;

    private GetProfileUseCase getProfileService;

    @BeforeEach
    void setUp() {
        getProfileService = new GetProfileService(loadProfilePort);
    }

    @Test
    void should_return_profile_when_found() {
        String idStr = "550e8400-e29b-41d4-a716-446655440000";
        var expectedProfile = Profile.create(
            new ProfileId(idStr), 
            new Email("found@example.com"), 
            new DisplayName("Found User")
        );
        
        when(loadProfilePort.findById(idStr)).thenReturn(Optional.of(expectedProfile));
        
        Optional<Profile> result = getProfileService.execute(idStr);
        
        assertThat(result).isPresent();
        assertThat(result.get().id().value()).isEqualTo(idStr);
        verify(loadProfilePort, times(1)).findById(idStr);
    }

    @Test
    void should_return_empty_when_not_found() {
        String notFoundId = "non-existent-id";
        
        when(loadProfilePort.findById(notFoundId)).thenReturn(Optional.empty());
        
        Optional<Profile> result = getProfileService.execute(notFoundId);
        
        assertThat(result).isEmpty();
        verify(loadProfilePort, times(1)).findById(notFoundId);
    }
}

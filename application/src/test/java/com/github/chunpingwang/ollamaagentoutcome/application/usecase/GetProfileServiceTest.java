package com.github.chunpingwang.ollamaagentoutcome.application.usecase;



import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import java.util.Optional;
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
        String idStr = "550e8400-e29b-41d4-a716-446655440000";
        Profile expectedProfile = createSampleProfile(idStr, "test@example.com", "Tester");
        
        when(loadProfilePort.findById(idStr)).thenReturn(Optional.of(expectedProfile));

        Optional<Profile> result = getProfileService.findById(idStr);

        assertThat(result).isPresent();
        assertThat(result.get().id().value()).isEqualTo(idStr);
        assertThat(result.get().email().address()).isEqualTo("test@example.com");
        verify(loadProfilePort).findById(idStr);
    }

    @Test
    void should_throw_exception_when_not_found() {
        String notFoundId = "non-existent-id";
        
        when(loadProfilePort.findById(notFoundId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> getProfileService.findById(notFoundId))
            .isInstanceOf(ProfileNotFoundException.class)
            .hasMessageContaining(notFoundId);
            
        verify(loadProfilePort).findById(notFoundId);
    }
    
    private Profile createSampleProfile(String id, String email, String displayName) {
        return Profile.create(
            new ProfileId(id), 
            new Email(email), 
            new DisplayName(displayName)
        );
    }
}

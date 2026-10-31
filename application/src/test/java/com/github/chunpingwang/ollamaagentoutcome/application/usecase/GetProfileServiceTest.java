package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
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
    private LoadProfilePort loadProfilePort;

    @InjectMocks
    private GetProfileService getProfileService;

    @Test
    void should_return_profile_when_found() {
        ProfileId id = new ProfileId(UUID.randomUUID());
        Email email = new Email("found@example.com");
        DisplayName displayName = new DisplayName("Found User");
        
        Profile expectedProfile = new Profile(id, email, displayName);
        when(loadProfilePort.findById(id)).thenReturn(Optional.of(expectedProfile));

        Optional<Profile> result = getProfileService.execute(id);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(id);
        assertThat(result.get().getEmail().getValue()).isEqualTo(email.getValue());
        verify(loadProfilePort).findById(id);
    }

    @Test
    void should_return_empty_optional_when_not_found() {
        ProfileId nonExistentId = new ProfileId(UUID.randomUUID());
        when(loadProfilePort.findById(nonExistentId)).thenReturn(Optional.empty());

        Optional<Profile> result = getProfileService.execute(nonExistentId);

        assertThat(result).isEmpty();
        verify(loadProfilePort).findById(nonExistentId);
    }
}

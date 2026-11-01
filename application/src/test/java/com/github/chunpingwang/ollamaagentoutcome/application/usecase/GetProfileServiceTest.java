package com.github.chunpingwang.ollamaagentoutcome.application.usecase;



import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class GetProfileServiceTest {

    @Mock
    private LoadProfilePort loadProfilePort;

    @InjectMocks
    private GetProfileService getProfileService;

    @Test
    void should_return_profile_when_found() {
        String id = UUID.randomUUID().toString();
        Email email = new Email("test@example.com");
        DisplayName displayName = new DisplayName("User");
        Profile expectedProfile = Profile.create(new ProfileId(id), email, displayName);

        when(loadProfilePort.findById(id)).thenReturn(Optional.of(expectedProfile));

        // Act
        Optional<Profile> result = getProfileService.execute(id);

        assertThat(result).isPresent().contains(expectedProfile);
        verify(loadProfilePort).findById(id);
    }

    @Test
    void should_throw_exception_when_not_found() {
        String id = "non-existent-id";

        when(loadProfilePort.findById(id)).thenReturn(Optional.empty());

        var result = getProfileService.execute(id);
        
        assertThat(result).isEmpty();
        verify(loadProfilePort).findById(id);
    }
}

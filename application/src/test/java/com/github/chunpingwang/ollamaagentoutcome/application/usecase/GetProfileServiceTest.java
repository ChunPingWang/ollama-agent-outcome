package com.github.chunpingwang.ollamaagentoutcome.application.usecase;



import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;

/**
 * GetProfileService 的單元測試。
 */
@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class GetProfileServiceTest {

    @Mock
    private LoadProfilePort loadProfilePort;

    @InjectMocks
    private GetProfileService getProfileService;

    @Test
    void should_return_profile_when_found() {
        // Arrange
        UUID id = UUID.randomUUID();
        Profile expectedProfile = new Profile(
            new ProfileId(id), 
            new Email("user@example.com"), 
            new DisplayName("User")
        );

        when(loadProfilePort.findById(new ProfileId(id))).thenReturn(Optional.of(expectedProfile));

        // Act
        Optional<Profile> result = getProfileService.execute(new ProfileId(id));

        // Assert
        assertThat(result).isPresent().contains(expectedProfile);
        verify(loadProfilePort).findById(new ProfileId(id));
    }

    @Test
    void should_return_empty_optional_when_not_found() {
        // Arrange
        UUID id = UUID.randomUUID();

        when(loadProfilePort.findById(new ProfileId(id))).thenReturn(Optional.empty());

        // Act
        Optional<Profile> result = getProfileService.execute(new ProfileId(id));

        // Assert
        assertThat(result).isEmpty();
        verify(loadProfilePort).findById(new ProfileId(id));
    }
}

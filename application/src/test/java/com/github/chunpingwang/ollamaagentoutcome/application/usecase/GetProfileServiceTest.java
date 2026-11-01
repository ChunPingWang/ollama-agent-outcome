package com.github.chunpingwang.ollamaagentoutcome.application.usecase;



import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
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
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class GetProfileServiceTest {

    @Mock
    private LoadProfilePort loadProfilePort;

    @InjectMocks
    private GetProfileService getProfileService;

    @Test
    void should_return_profile_when_exists() {
        UUID id = UUID.randomUUID();
        Profile profile = Profile.create(
            new ProfileId(id),
            new Email("user@example.com"),
            new DisplayName("John Doe")
        );

        when(loadProfilePort.findById(id)).thenReturn(Optional.of(profile));

        Optional<Profile> result = getProfileService.execute(id);

        assertThat(result).isPresent().contains(profile);
    }

    @Test
    void should_return_empty_when_not_found() {
        UUID id = UUID.randomUUID();

        when(loadProfilePort.findById(id)).thenReturn(Optional.empty());

        Optional<Profile> result = getProfileService.execute(id);

        assertThat(result).isEmpty();
    }
}

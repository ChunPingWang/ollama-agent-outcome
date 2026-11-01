package com.github.chunpingwang.ollamaagentoutcome.application.usecase;



import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

import java.util.Optional;
import java.util.UUID;

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

    private GetProfileService service;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        service = new GetProfileService(loadProfilePort);
    }

    @Test
    void should_return_profile_when_found() {
        UUID testUuid = UUID.randomUUID();
        ProfileId id = new ProfileId(testUuid);
        Email email = new Email("user@example.com");
        DisplayName displayName = new DisplayName("Display Name");
        
        Profile expectedProfile = new Profile(id, email, displayName);
        
        when(loadProfilePort.findById(id)).thenReturn(Optional.of(expectedProfile));

        Optional<Profile> result = service.execute(id);

        assertThat(result).isPresent().get().isEqualTo(expectedProfile);
        verify(loadProfilePort).findById(id);
    }

    @Test
    void should_return_empty_optional_when_not_found() {
        UUID nonExistentUuid = UUID.randomUUID();
        ProfileId id = new ProfileId(nonExistentUuid);
        
        when(loadProfilePort.findById(id)).thenReturn(Optional.empty());

        Optional<Profile> result = service.execute(id);

        assertThat(result).isEmpty();
        verify(loadProfilePort).findById(id);
    }
}

package com.github.chunpingwang.ollamaagentoutcome.application.usecase;



import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class GetProfileServiceTest {

    @Mock
    private LoadProfilePort loadProfilePort;

    private GetProfileService getProfileService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        getProfileService = new GetProfileService(loadProfilePort);
    }

    @Test
    void should_return_profile_when_exists() {
        String idStr = UUID.randomUUID().toString();
        ProfileId id = new ProfileId(idStr);
        Email email = new Email("test@example.com");
        DisplayName displayName = new DisplayName("User Name");
        
        Profile profile = Profile.create(id, email, displayName);
        
        when(loadProfilePort.findById(idStr)).thenReturn(Optional.of(profile));

        Optional<Profile> result = getProfileService.execute(idStr);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(profile);
    }

    @Test
    void should_return_empty_when_not_found() {
        String idStr = UUID.randomUUID().toString();
        
        when(loadProfilePort.findById(idStr)).thenReturn(Optional.empty());

        Optional<Profile> result = getProfileService.execute(idStr);

        assertThat(result).isEmpty();
    }
}

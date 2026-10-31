package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.entity.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.exception.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.ProfileId;

@ExtendWith(MockitoExtension.class)
class GetProfileServiceTest {

    @Mock
    private LoadProfilePort loadProfilePort;

    @InjectMocks
    private GetProfileService getProfileService; // Will be created in next step

    @Test
    void should_return_profile_when_exists() {
        var id = UUID.randomUUID();
        var profileId = new ProfileId(id);
        var email = new Email("test@example.com");
        var displayName = new DisplayName("User");
        var expectedProfile = new Profile(profileId, email, displayName);

        when(loadProfilePort.loadById(profileId)).thenReturn(Optional.of(expectedProfile));

        var result = getProfileService.findById(profileId);

        assertThat(result).isPresent().get().isEqualTo(expectedProfile);
    }

    @Test
    void should_throw_exception_when_not_found() {
        var id = UUID.randomUUID();
        var profileId = new ProfileId(id);

        when(loadProfilePort.loadById(profileId)).thenReturn(Optional.empty());

        org.assertj.core.api.Assertions.assertThatThrownBy(() -> getProfileService.findById(profileId))
                .isInstanceOf(ProfileNotFoundException.class)
                .hasMessageContaining(id.toString());
    }
}

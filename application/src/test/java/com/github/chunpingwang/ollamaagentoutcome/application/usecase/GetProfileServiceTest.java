package com.github.chunpingwang.ollamaagentoutcome.application.usecase;



import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class GetProfileServiceTest {

    @Mock
    private LoadProfilePort loadProfilePort;

    @InjectMocks
    private GetProfileService getProfileService;

    @Test
    void should_return_profile_when_found() {
        // given
        String id = UUID.randomUUID().toString();
        ProfileId profileId = new ProfileId(id);
        Email email = new Email("user@example.com");
        DisplayName displayName = new DisplayName("User Name");
        
        var domainEntity = com.github.chunpingwang.ollamaagentoutcome.domain.Profile.create(profileId, email, displayName);
            
        when(loadProfilePort.findById(id)).thenReturn(Optional.of(domainEntity));

        // when
        GetProfileUseCase.GetProfileResponse result = getProfileService.findById(id);

        // then
        assertThat(result.id()).isEqualTo(id);
        assertThat(result.email()).isEqualTo(email.address());
        assertThat(result.displayName()).isEqualTo(displayName.name());
    }

    @Test
    void should_throw_exception_when_not_found() {
        // given
        String id = "non-existent-id";
        when(loadProfilePort.findById(id)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> getProfileService.findById(id))
            .isInstanceOf(ProfileNotFoundException.class)
            .hasMessageContaining("profile not found with id: " + id);
    }
}

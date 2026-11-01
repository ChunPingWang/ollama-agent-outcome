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
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileResponse;
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
        // given
        String id = UUID.randomUUID().toString();
        Profile profile = createValidProfile(id);
        
        when(loadProfilePort.findById(id)).thenReturn(Optional.of(profile));

        // when
        Optional<GetProfileResponse> result = getProfileService.execute(id);

        // then
        assertThat(result).isPresent();
        GetProfileResponse response = result.get();
        assertThat(response.id()).isEqualTo(id);
        assertThat(response.email()).isEqualTo("user@example.com");
        assertThat(response.displayName()).isEqualTo("John Doe");
    }

    @Test
    void should_return_empty_when_not_found() {
        // given
        String id = "non-existent-id";
        when(loadProfilePort.findById(id)).thenReturn(Optional.empty());

        // when & then
        var result = getProfileService.execute(id);
        assertThat(result).isEmpty();
    }

    private Profile createValidProfile(String id) {
        return Profile.create(
            new ProfileId(id),
            Email.of("user@example.com"),
            DisplayName.of("John Doe")
        );
    }
}

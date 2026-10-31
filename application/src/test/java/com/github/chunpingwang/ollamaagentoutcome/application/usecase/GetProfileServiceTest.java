package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByIdPort;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Age;

@ExtendWith(MockitoExtension.class)
class GetProfileServiceTest {

    @Mock
    private FindProfileByIdPort findProfileByIdPort;

    @InjectMocks
    private GetProfileService getProfileService;

    @Test
    void should_return_profile_when_found() {
        // Given
        UUID uuid = UUID.randomUUID();
        ProfileId profileId = new ProfileId(uuid);
        Profile expectedProfile = new Profile(
                profileId,
                new Email("user@example.com"),
                new Name("John Doe"),
                new Age(25)
        );

        when(findProfileByIdPort.findById(profileId)).thenReturn(Optional.of(expectedProfile));

        // When
        Profile result = getProfileService.getProfile(profileId);

        // Then
        assertThat(result).isEqualTo(expectedProfile);
        assertThat(result.getId()).isEqualTo(profileId);
    }

    @Test
    void should_throw_exception_when_profile_not_found() {
        // Given
        UUID uuid = UUID.randomUUID();
        ProfileId profileId = new ProfileId(uuid);

        when(findProfileByIdPort.findById(profileId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> getProfileService.getProfile(profileId))
                .isInstanceOf(ProfileNotFoundException.class)
                .hasMessageContaining("Profile not found");
    }
}

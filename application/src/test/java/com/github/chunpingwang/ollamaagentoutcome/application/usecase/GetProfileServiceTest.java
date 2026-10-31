package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Age;

@ExtendWith(MockitoExtension.class)
class GetProfileServiceTest {

    @Mock
    private LoadProfilePort loadProfilePort;

    @InjectMocks
    private GetProfileService getProfileService;

    @Test
    void should_return_profile_when_found() {
        // Given
        String profileId = "profile-123";
        ProfileId id = new ProfileId(profileId);
        Email email = new Email("user@example.com");
        Name name = new Name("John Doe");
        Age age = new Age(30);
        Profile profile = new Profile(id, email, name, age);

        when(loadProfilePort.findById(profileId)).thenReturn(Optional.of(profile));

        // When
        Profile result = getProfileService.execute(profileId);

        // Then
        assertThat(result).isEqualTo(profile);
        assertThat(result.id()).isEqualTo(id);
        assertThat(result.email()).isEqualTo(email);
        assertThat(result.name()).isEqualTo(name);
        assertThat(result.age()).isEqualTo(age);
    }

    @Test
    void should_throw_profile_not_found_exception_when_not_found() {
        // Given
        String profileId = "non-existent-id";
        when(loadProfilePort.findById(profileId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> getProfileService.execute(profileId))
                .isInstanceOf(ProfileNotFoundException.class)
                .hasMessageContaining("Profile not found: " + profileId);
    }
}

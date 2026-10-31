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

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.Bio;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.ProfileId;

@ExtendWith(MockitoExtension.class)
class GetProfileServiceTest {

    @Mock
    private LoadProfilePort loadProfilePort;

    @InjectMocks
    private GetProfileService getProfileService;

    @Test
    void should_return_profile_when_found() {
        // Given
        UUID id = UUID.randomUUID();
        Profile expectedProfile = new Profile(
            new ProfileId(id),
            new Email("test@example.com"),
            new DisplayName("Tester"),
            new Bio("Hello")
        );
        
        when(loadProfilePort.findById(id)).thenReturn(Optional.of(expectedProfile));

        // When
        Optional<Profile> result = getProfileService.findById(id);

        // Then
        assertThat(result).isPresent().contains(expectedProfile);
    }

    @Test
    void should_return_empty_optional_when_not_found() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(loadProfilePort.findById(nonExistentId)).thenReturn(Optional.empty());

        // When
        Optional<Profile> result = getProfileService.findById(nonExistentId);

        // Then
        assertThat(result).isEmpty();
    }
}

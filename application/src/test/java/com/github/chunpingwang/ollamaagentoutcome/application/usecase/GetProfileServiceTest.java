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

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

@ExtendWith(MockitoExtension.class)
class GetProfileServiceTest {

    @Mock
    private LoadProfilePort loadProfilePort;

    @InjectMocks
    private GetProfileService getProfileService;

    @Test
    void should_return_profile_when_exists() {
        // Given
        UUID uuid = UUID.randomUUID();
        ProfileId id = new ProfileId(uuid);
        Profile expectedProfile = new Profile(id, "王小明", "xiaoming@example.com", "測試");
        when(loadProfilePort.findById(id)).thenReturn(Optional.of(expectedProfile));

        // When
        Profile result = getProfileService.execute(id);

        // Then
        assertThat(result).isEqualTo(expectedProfile);
    }

    @Test
    void should_throw_profile_not_found_exception_when_not_exists() {
        // Given
        UUID uuid = UUID.randomUUID();
        ProfileId id = new ProfileId(uuid);
        when(loadProfilePort.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> getProfileService.execute(id))
                .isInstanceOf(ProfileNotFoundException.class)
                .hasMessageContaining("not found");
    }
}

package com.github.chunpingwang.ollamaagentoutcome.application.usecase;



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

import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class UpdateProfileServiceTest {

    @Mock
    private LoadProfilePort loadProfilePort;

    @Mock
    private SaveProfilePort saveProfilePort;

    @InjectMocks
    private UpdateProfileService updateProfileService;

    @Test
    void should_update_profile_when_exists() {
        UUID id = UUID.randomUUID();
        String newEmail = "new@example.com";
        String newDisplayName = "New Name";

        Profile existingProfile = Profile.create(
            new ProfileId(id),
            new Email("old@example.com"),
            new DisplayName("Old Name")
        );

        when(loadProfilePort.findById(id)).thenReturn(Optional.of(existingProfile));
        doNothing().when(saveProfilePort).save(org.mockito.ArgumentMatchers.any());

        updateProfileService.execute(id, newEmail, newDisplayName);

        verify(saveProfilePort).save(org.mockito.ArgumentMatchers.argThat(profile -> 
            profile.id().id().equals(id) &&
            profile.email().value().equals(newEmail) &&
            profile.displayName().value().equals(newDisplayName)
        ));
    }

    @Test
    void should_throw_exception_when_not_found() {
        UUID id = UUID.randomUUID();

        when(loadProfilePort.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> updateProfileService.execute(id, "test@test.com", "Name"))
            .isInstanceOf(ProfileNotFoundException.class)
            .hasMessageContaining("profile not found");
    }
}

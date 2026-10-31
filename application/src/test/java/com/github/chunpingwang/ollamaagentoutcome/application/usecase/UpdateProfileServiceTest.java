package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.Bio;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.service.ProfileNotFoundException;

@ExtendWith(MockitoExtension.class)
class UpdateProfileServiceTest {

    @Mock
    private LoadProfilePort loadProfilePort;

    @Mock
    private SaveProfilePort saveProfilePort;

    @InjectMocks
    private UpdateProfileService updateProfileService;

    @Test
    void should_update_profile_successfully() {
        // Given
        UUID id = UUID.randomUUID();
        Profile existingProfile = new Profile(
            new ProfileId(id),
            new Email("test@example.com"),
            new DisplayName("Old Name"),
            new Bio("Old Bio")
        );
        
        when(loadProfilePort.findById(id)).thenReturn(Optional.of(existingProfile));
        doNothing().when(saveProfilePort).save(any(Profile.class));

        String newName = "New Name";
        String newBio = "Updated Bio";

        // When
        updateProfileService.execute(id, newName, newBio);

        // Then
        verify(loadProfilePort).findById(id);
        verify(saveProfilePort).save(any(Profile.class));
    }

    @Test
    void should_throw_exception_when_profile_not_found_for_update() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(loadProfilePort.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> updateProfileService.execute(nonExistentId, "Name", "Bio"))
                .isInstanceOf(ProfileNotFoundException.class)
                .hasMessageContaining("profile not found with id: " + nonExistentId);
    }
}

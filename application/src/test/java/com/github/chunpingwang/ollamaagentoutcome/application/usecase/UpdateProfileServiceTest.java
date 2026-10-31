package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.exception.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.exception.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
        ProfileId profileId = new ProfileId(id);
        Email oldEmail = new Email("old@example.com");
        Profile existingProfile = new Profile(profileId, "Old Name", oldEmail);

        Email newEmail = new Email("new@example.com");
        Profile updatedProfile = new Profile(profileId, "New Name", newEmail);

        when(loadProfilePort.findById(profileId)).thenReturn(Optional.of(existingProfile));
        when(saveProfilePort.save(any(Profile.class))).thenReturn(updatedProfile);

        // When
        updateProfileService.update(profileId, "New Name", "new@example.com");

        // Then
        verify(saveProfilePort).save(any(Profile.class));
    }

    @Test
    void should_throw_exception_when_profile_not_found() {
        // Given
        UUID id = UUID.randomUUID();
        ProfileId profileId = new ProfileId(id);

        when(loadProfilePort.findById(profileId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> updateProfileService.update(profileId, "New Name", "new@example.com"))
                .isInstanceOf(ProfileNotFoundException.class)
                .hasMessageContaining("Profile not found");
    }

    @Test
    void should_throw_duplicate_email_exception_when_email_exists() {
        // Given
        UUID id = UUID.randomUUID();
        ProfileId profileId = new ProfileId(id);
        Email oldEmail = new Email("old@example.com");
        Profile existingProfile = new Profile(profileId, "Old Name", oldEmail);

        Email duplicateEmail = new Email("duplicate@example.com");
        
        when(loadProfilePort.findById(profileId)).thenReturn(Optional.of(existingProfile));
        when(saveProfilePort.save(any(Profile.class)))
                .thenThrow(new DuplicateEmailException(duplicateEmail));

        // When & Then
        assertThatThrownBy(() -> updateProfileService.update(profileId, "New Name", "duplicate@example.com"))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("Email already exists");
    }
}

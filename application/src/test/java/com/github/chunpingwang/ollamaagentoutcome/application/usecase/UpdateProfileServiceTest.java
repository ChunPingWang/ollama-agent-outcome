package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileRequest;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Age;

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
        String profileId = "profile-123";
        ProfileId id = new ProfileId(profileId);
        Email oldEmail = new Email("old@example.com");
        Name oldName = new Name("Old Name");
        Age oldAge = new Age(30);
        Profile existingProfile = new Profile(id, oldEmail, oldName, oldAge);

        String newEmail = "new@example.com";
        String newName = "New Name";
        int newAge = 31;
        UpdateProfileRequest request = new UpdateProfileRequest(newEmail, newName, newAge);

        when(loadProfilePort.findById(profileId)).thenReturn(Optional.of(existingProfile));
        when(saveProfilePort.save(any(Profile.class))).thenAnswer(invocation -> {
            Profile p = invocation.getArgument(0);
            return p;
        });

        // When
        Profile result = updateProfileService.execute(profileId, request);

        // Then
        assertThat(result).isNotSameAs(existingProfile);
        assertThat(result.id()).isEqualTo(id);
        assertThat(result.email().getValue()).isEqualTo(newEmail);
        assertThat(result.name().getValue()).isEqualTo(newName);
        assertThat(result.age().getValue()).isEqualTo(newAge);

        verify(loadProfilePort).findById(profileId);
        verify(saveProfilePort).save(any(Profile.class));
    }

    @Test
    void should_throw_profile_not_found_exception_when_updating_non_existent_profile() {
        // Given
        String profileId = "non-existent-id";
        String newEmail = "new@example.com";
        String newName = "New Name";
        int newAge = 31;
        UpdateProfileRequest request = new UpdateProfileRequest(newEmail, newName, newAge);

        when(loadProfilePort.findById(profileId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> updateProfileService.execute(profileId, request))
                .isInstanceOf(ProfileNotFoundException.class)
                .hasMessageContaining("Profile not found: " + profileId);

        verify(saveProfilePort, never()).save(any(Profile.class));
    }

    @Test
    void should_throw_duplicate_email_exception_when_updating_to_existing_email() {
        // Given
        String profileId = "profile-123";
        ProfileId id = new ProfileId(profileId);
        Email oldEmail = new Email("old@example.com");
        Name oldName = new Name("Old Name");
        Age oldAge = new Age(30);
        Profile existingProfile = new Profile(id, oldEmail, oldName, oldAge);

        String duplicateEmail = "duplicate@example.com";
        String newName = "New Name";
        int newAge = 31;
        UpdateProfileRequest request = new UpdateProfileRequest(duplicateEmail, newName, newAge);

        when(loadProfilePort.findById(profileId)).thenReturn(Optional.of(existingProfile));
        when(saveProfilePort.save(any(Profile.class))).thenThrow(new DuplicateEmailException(duplicateEmail));

        // When & Then
        assertThatThrownBy(() -> updateProfileService.execute(profileId, request))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("Email already exists: " + duplicateEmail);
    }
}

package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase.UpdateProfileRequest;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByEmailPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByIdPort;
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
    private FindProfileByIdPort findProfileByIdPort;

    @Mock
    private FindProfileByEmailPort findProfileByEmailPort;

    @Mock
    private SaveProfilePort saveProfilePort;

    @InjectMocks
    private UpdateProfileService updateProfileService;

    @Test
    void should_update_profile_successfully() {
        // Given
        UUID uuid = UUID.randomUUID();
        ProfileId profileId = new ProfileId(uuid);
        Email existingEmail = new Email("old@example.com");
        Name oldName = new Name("Old Name");
        Age oldAge = new Age(25);
        Profile existingProfile = new Profile(profileId, existingEmail, oldName, oldAge);

        UpdateProfileRequest request = new UpdateProfileRequest("new@example.com", "New Name", 26);
        Email newEmail = new Email("new@example.com");
        Name newName = new Name("New Name");
        Age newAge = new Age(26);
        Profile updatedProfile = new Profile(profileId, newEmail, newName, newAge);

        when(findProfileByIdPort.findById(profileId)).thenReturn(Optional.of(existingProfile));
        when(findProfileByEmailPort.findByEmail(newEmail)).thenReturn(Optional.empty());
        when(saveProfilePort.save(any(Profile.class))).thenReturn(updatedProfile);

        // When
        Profile result = updateProfileService.updateProfile(profileId, request);

        // Then
        assertThat(result).isEqualTo(updatedProfile);
        verify(saveProfilePort).save(any(Profile.class));
    }

    @Test
    void should_throw_exception_when_profile_not_found() {
        // Given
        UUID uuid = UUID.randomUUID();
        ProfileId profileId = new ProfileId(uuid);

        when(findProfileByIdPort.findById(profileId)).thenReturn(Optional.empty());

        UpdateProfileRequest request = new UpdateProfileRequest("user@example.com", "New Name", 26);

        // When & Then
        assertThatThrownBy(() -> updateProfileService.updateProfile(profileId, request))
                .isInstanceOf(ProfileNotFoundException.class)
                .hasMessageContaining("Profile not found");

        verify(findProfileByEmailPort, never()).findByEmail(any());
        verify(saveProfilePort, never()).save(any(Profile.class));
    }

    @Test
    void should_throw_exception_when_email_already_exists() {
        // Given
        UUID uuid = UUID.randomUUID();
        ProfileId profileId = new ProfileId(uuid);
        Email existingEmail = new Email("old@example.com");
        Name oldName = new Name("Old Name");
        Age oldAge = new Age(25);
        Profile existingProfile = new Profile(profileId, existingEmail, oldName, oldAge);

        UpdateProfileRequest request = new UpdateProfileRequest("duplicate@example.com", "New Name", 26);
        Email duplicateEmail = new Email("duplicate@example.com");
        Profile duplicateProfile = new Profile(new ProfileId(UUID.randomUUID()), duplicateEmail, new Name("Dup"), new Age(30));

        when(findProfileByIdPort.findById(profileId)).thenReturn(Optional.of(existingProfile));
        when(findProfileByEmailPort.findByEmail(duplicateEmail)).thenReturn(Optional.of(duplicateProfile));

        // When & Then
        assertThatThrownBy(() -> updateProfileService.updateProfile(profileId, request))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("Email already exists");

        verify(saveProfilePort, never()).save(any(Profile.class));
    }
}

package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chunpingwang.ollamaagentoutcome.application.UpdateProfileRequest;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

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
        // given
        UUID id = UUID.randomUUID();
        ProfileId profileId = new ProfileId(id);
        Profile existingProfile = Profile.create(profileId, "John Doe", "john@example.com", "old note");
        UpdateProfileRequest request = new UpdateProfileRequest("Jane Doe", "jane@example.com", "new note");
        Profile updatedProfile = Profile.create(profileId, "Jane Doe", "jane@example.com", "new note");

        when(loadProfilePort.findById(profileId)).thenReturn(Optional.of(existingProfile));
        when(saveProfilePort.save(any(Profile.class))).thenReturn(updatedProfile);

        // when
        Profile result = updateProfileService.execute(profileId, request);

        // then
        assertThat(result).isEqualTo(updatedProfile);
    }

    @Test
    void should_throw_profile_not_found_exception_when_not_found() {
        // given
        UUID id = UUID.randomUUID();
        ProfileId profileId = new ProfileId(id);
        UpdateProfileRequest request = new UpdateProfileRequest("Jane Doe", "jane@example.com", "new note");
        when(loadProfilePort.findById(profileId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> updateProfileService.execute(profileId, request))
                .isInstanceOf(ProfileNotFoundException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void should_throw_duplicate_email_exception_when_email_exists() {
        // given
        UUID id = UUID.randomUUID();
        ProfileId profileId = new ProfileId(id);
        Profile existingProfile = Profile.create(profileId, "John Doe", "john@example.com", "old note");
        UpdateProfileRequest request = new UpdateProfileRequest("Jane Doe", "duplicate@example.com", "new note");
        when(loadProfilePort.findById(profileId)).thenReturn(Optional.of(existingProfile));
        when(saveProfilePort.save(any(Profile.class))).thenThrow(new DuplicateEmailException("Email already exists"));

        // when & then
        assertThatThrownBy(() -> updateProfileService.execute(profileId, request))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessage("Email already exists");
    }
}

package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileRequest;
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
        // Given
        UUID uuid = UUID.randomUUID();
        ProfileId id = new ProfileId(uuid);
        Profile existingProfile = new Profile(id, "王小明", "old@example.com", "舊備註");
        when(loadProfilePort.findById(id)).thenReturn(Optional.of(existingProfile));

        UpdateProfileRequest request = new UpdateProfileRequest("王大明", "new@example.com", "新備註");
        Profile updatedProfile = new Profile(id, "王大明", "new@example.com", "新備註");
        when(saveProfilePort.save(any(Profile.class))).thenReturn(updatedProfile);

        // When
        Profile result = updateProfileService.execute(id, request);

        // Then
        assertThat(result).isEqualTo(updatedProfile);
        verify(saveProfilePort).save(any(Profile.class));
    }

    @Test
    void should_throw_profile_not_found_exception_when_updating_non_existent() {
        // Given
        UUID uuid = UUID.randomUUID();
        ProfileId id = new ProfileId(uuid);
        when(loadProfilePort.findById(id)).thenReturn(Optional.empty());

        UpdateProfileRequest request = new UpdateProfileRequest("王大明", "new@example.com", "新備註");

        // When & Then
        assertThatThrownBy(() -> updateProfileService.execute(id, request))
                .isInstanceOf(ProfileNotFoundException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void should_throw_duplicate_email_exception_when_email_exists_during_update() {
        // Given
        UUID uuid = UUID.randomUUID();
        ProfileId id = new ProfileId(uuid);
        Profile existingProfile = new Profile(id, "王小明", "old@example.com", "舊備註");
        when(loadProfilePort.findById(id)).thenReturn(Optional.of(existingProfile));

        UpdateProfileRequest request = new UpdateProfileRequest("王大明", "duplicate@example.com", "新備註");
        doThrow(new DuplicateEmailException("Email already exists")).when(saveProfilePort).save(any(Profile.class));

        // When & Then
        assertThatThrownBy(() -> updateProfileService.execute(id, request))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("Email already exists");
    }
}

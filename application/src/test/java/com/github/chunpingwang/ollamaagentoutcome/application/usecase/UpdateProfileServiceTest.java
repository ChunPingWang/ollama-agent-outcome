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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.ExistsByEmailPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;

/**
 * 測試 UpdateProfileService 的業務邏輯。
 */
@ExtendWith(MockitoExtension.class)
class UpdateProfileServiceTest {

    @Mock
    private LoadProfilePort loadProfilePort;

    @Mock
    private ExistsByEmailPort existsByEmailPort;

    @Mock
    private SaveProfilePort saveProfilePort;

    private UpdateProfileService updateProfileService;

    @Test
    void should_update_profile_successfully() {
        // Given
        UUID id = UUID.randomUUID();
        String oldName = "張三";
        String oldEmail = "old@example.com";
        String newName = "張三豐";
        String newEmail = "new@example.com";
        
        Profile existingProfile = new Profile(new ProfileId(id), oldName, new Email(oldEmail));
        
        UpdateProfileCommand command = new UpdateProfileCommand(id, newName, newEmail);
        
        when(loadProfilePort.findById(new ProfileId(id))).thenReturn(Optional.of(existingProfile));
        when(existsByEmailPort.existsByEmail(any(Email.class))).thenReturn(false);
        when(saveProfilePort.save(any(Profile.class))).thenReturn(
            new Profile(new ProfileId(id), newName, new Email(newEmail))
        );
        
        updateProfileService = new UpdateProfileService(loadProfilePort, existsByEmailPort, saveProfilePort);

        // When
        updateProfileService.execute(command);

        // Then
        verify(loadProfilePort).findById(new ProfileId(id));
        verify(existsByEmailPort).existsByEmail(new Email(newEmail));
        verify(saveProfilePort).save(any(Profile.class));
    }

    @Test
    void should_throw_profile_not_found_exception_when_profile_not_exists() {
        // Given
        UUID id = UUID.randomUUID();
        String newName = "張三";
        String newEmail = "new@example.com";
        
        UpdateProfileCommand command = new UpdateProfileCommand(id, newName, newEmail);
        
        when(loadProfilePort.findById(new ProfileId(id))).thenReturn(Optional.empty());
        
        updateProfileService = new UpdateProfileService(loadProfilePort, existsByEmailPort, saveProfilePort);

        // When & Then
        assertThatThrownBy(() -> updateProfileService.execute(command))
                .isInstanceOf(ProfileNotFoundException.class)
                .hasMessageContaining("not found");

        verify(existsByEmailPort, never()).existsByEmail(any(Email.class));
        verify(saveProfilePort, never()).save(any(Profile.class));
    }

    @Test
    void should_throw_duplicate_email_exception_when_new_email_exists() {
        // Given
        UUID id = UUID.randomUUID();
        String newName = "張三";
        String newEmail = "duplicate@example.com";
        
        Profile existingProfile = new Profile(new ProfileId(id), "Old Name", new Email("old@example.com"));
        
        UpdateProfileCommand command = new UpdateProfileCommand(id, newName, newEmail);
        
        when(loadProfilePort.findById(new ProfileId(id))).thenReturn(Optional.of(existingProfile));
        when(existsByEmailPort.existsByEmail(any(Email.class))).thenReturn(true);
        
        updateProfileService = new UpdateProfileService(loadProfilePort, existsByEmailPort, saveProfilePort);

        // When & Then
        assertThatThrownBy(() -> updateProfileService.execute(command))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("duplicate@example.com");

        verify(saveProfilePort, never()).save(any(Profile.class));
    }
}

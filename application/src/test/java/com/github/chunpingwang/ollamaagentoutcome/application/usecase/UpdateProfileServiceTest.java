package com.github.chunpingwang.ollamaagentoutcome.application.usecase;



import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
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

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileCommand;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.ExistsByEmailPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class UpdateProfileServiceTest {

    @Mock
    private LoadProfilePort loadProfilePort;

    @Mock
    private SaveProfilePort saveProfilePort;

    @Mock
    private ExistsByEmailPort existsByEmailPort;

    @InjectMocks
    private UpdateProfileService updateProfileService;

    @Test
    void should_update_profile_when_exists_and_email_is_unique() {
        // Given
        String id = UUID.randomUUID().toString();
        Email oldEmail = new Email("old@example.com");
        DisplayName oldName = new DisplayName("Old Name");
        Profile existingProfile = new Profile(id, oldEmail, oldName);
        
        when(loadProfilePort.findById(id)).thenReturn(Optional.of(existingProfile));
        when(existsByEmailPort.existsByEmail("new@example.com")).thenReturn(false);
        
        // Mock the updated profile returned by domain logic or port
        Email newEmail = new Email("new@example.com");
        DisplayName newName = new DisplayName("New Name");
        Profile updatedProfile = new Profile(id, newEmail, newName);
        when(saveProfilePort.save(any(Profile.class))).thenReturn(updatedProfile);

        UpdateProfileCommand command = new UpdateProfileCommand(id, "new@example.com", "New Name");

        // When
        Profile result = updateProfileService.execute(command);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.email()).isEqualTo(newEmail);
        verify(saveProfilePort).save(any(Profile.class));
    }

    @Test
    void should_throw_not_found_exception_when_profile_does_not_exist() {
        // Given
        String id = UUID.randomUUID().toString();
        when(loadProfilePort.findById(id)).thenReturn(Optional.empty());

        UpdateProfileCommand command = new UpdateProfileCommand(id, "test@test.com", "Name");

        // When & Then
        assertThatThrownBy(() -> updateProfileService.execute(command))
                .isInstanceOf(ProfileNotFoundException.class)
                .hasMessageContaining("找不到識別碼為")
                .hasMessageContaining(id);

        verify(saveProfilePort, never()).save(any());
    }

    @Test
    void should_throw_duplicate_email_exception_when_new_email_exists_and_differs_from_current() {
        // Given
        String id = UUID.randomUUID().toString();
        Email currentEmail = new Email("current@example.com");
        DisplayName displayName = new DisplayName("Current User");
        Profile existingProfile = new Profile(id, currentEmail, displayName);
        
        when(loadProfilePort.findById(id)).thenReturn(Optional.of(existingProfile));
        when(existsByEmailPort.existsByEmail("duplicate@example.com")).thenReturn(true);

        UpdateProfileCommand command = new UpdateProfileCommand(id, "duplicate@example.com", "New Name");

        // When & Then
        assertThatThrownBy(() -> updateProfileService.execute(command))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("電子郵件已存在");

        verify(saveProfilePort, never()).save(any());
    }
    
    @Test
    void should_allow_same_email_update_without_checking_uniqueness_against_others() {
        // Given
        String id = UUID.randomUUID().toString();
        Email sameEmail = new Email("same@example.com");
        DisplayName oldName = new DisplayName("Old Name");
        Profile existingProfile = new Profile(id, sameEmail, oldName);
        
        when(loadProfilePort.findById(id)).thenReturn(Optional.of(existingProfile));
        
        // Even if existsByEmail returns true (which it will for the same email), 
        // logic should handle self-exclusion or simply not call check if email is unchanged.
        // Based on implementation: if !profile.email().address().equals(command.email()) then check.
        // So we don't need to mock existsByEmail here because it shouldn't be called.
        
        Email updatedEmail = new Email("same@example.com");
        DisplayName newName = new DisplayName("Updated Name");
        Profile updatedProfile = new Profile(id, updatedEmail, newName);
        when(saveProfilePort.save(any(Profile.class))).thenReturn(updatedProfile);

        UpdateProfileCommand command = new UpdateProfileCommand(id, "same@example.com", "Updated Name");

        // When
        Profile result = updateProfileService.execute(command);

        // Then
        assertThat(result).isNotNull();
        verify(existsByEmailPort, never()).existsByEmail(any());
    }
}

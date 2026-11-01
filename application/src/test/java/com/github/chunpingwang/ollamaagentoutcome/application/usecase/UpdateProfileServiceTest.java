package com.github.chunpingwang.ollamaagentoutcome.application.usecase;




import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
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

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.ExistsByEmailPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByIdPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class UpdateProfileServiceTest {

    @Mock
    private FindProfileByIdPort findProfileByIdPort;

    @Mock
    private ExistsByEmailPort existsByEmailPort;

    @Mock
    private SaveProfilePort saveProfilePort;

    @InjectMocks
    private UpdateProfileService updateProfileService;

    @Test
    void should_update_profile_successfully() {
        // Given
        UUID id = UUID.randomUUID();
        ProfileId profileId = new ProfileId(id);
        String oldEmail = "old@example.com";
        String newEmail = "new@example.com";
        String displayName = "Updated Name";
        
        Profile existingProfile = Profile.create(id, oldEmail, "Old Name");
        when(findProfileByIdPort.findById(profileId)).thenReturn(Optional.of(existingProfile));
        when(existsByEmailPort.existsByEmail(any(Email.class))).thenReturn(false);
        // 修正：save 方法有回傳值 (Profile)，不可使用 doNothing，應改用 when().thenReturn()
        when(saveProfilePort.save(any(Profile.class))).thenReturn(existingProfile);

        // When
        updateProfileService.execute(profileId, newEmail, displayName);

        // Then
        verify(findProfileByIdPort).findById(profileId);
        verify(existsByEmailPort).existsByEmail(new Email(newEmail));
        verify(saveProfilePort).save(any(Profile.class));
        
        assertThat(existingProfile.getEmailString()).isEqualTo(newEmail.toLowerCase());
        assertThat(existingProfile.getDisplayNameString()).isEqualTo(displayName);
    }

    @Test
    void should_throw_exception_when_not_found_for_update() {
        // Given
        UUID id = UUID.randomUUID();
        ProfileId profileId = new ProfileId(id);
        
        when(findProfileByIdPort.findById(profileId)).thenReturn(Optional.empty());

        // When & Then
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> 
            updateProfileService.execute(profileId, "test@test.com", "New")
        ).isInstanceOf(ProfileNotFoundException.class)
         .hasMessageContaining("not found");
         
        verify(findProfileByIdPort).findById(profileId);
        verify(existsByEmailPort, never()).existsByEmail(any());
        verify(saveProfilePort, never()).save(any());
    }

    @Test
    void should_throw_duplicate_email_exception_when_new_email_conflicts_with_other_id() {
        // Given
        UUID id = UUID.randomUUID();
        ProfileId profileId = new ProfileId(id);
        String conflictingEmail = "conflict@example.com";
        
        Profile existingProfile = Profile.create(id, "original@example.com", "Original Name");
        when(findProfileByIdPort.findById(profileId)).thenReturn(Optional.of(existingProfile));
        when(existsByEmailPort.existsByEmail(new Email(conflictingEmail))).thenReturn(true);

        // When & Then
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> 
            updateProfileService.execute(profileId, conflictingEmail, "New Name")
        ).isInstanceOf(DuplicateEmailException.class)
         .hasMessageContaining("already exists");
         
        verify(findProfileByIdPort).findById(profileId);
        verify(existsByEmailPort).existsByEmail(new Email(conflictingEmail));
        verify(saveProfilePort, never()).save(any());
    }
}

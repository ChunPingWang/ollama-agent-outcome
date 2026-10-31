package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

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

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Bio;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.ExistsByEmailPort;

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
    void should_update_profile_when_found() {
        UUID id = UUID.randomUUID();
        String oldName = "Old Name";
        String newName = "New Name";
        String email = "test@example.com";
        String bio = "Bio";

        Profile profile = new Profile(new ProfileId(id), new Name(oldName), new Email(email), new Bio(bio));
        
        when(loadProfilePort.findById(any(ProfileId.class))).thenReturn(Optional.of(profile));
        
        // Since we are updating with the SAME email, existsByEmail check is skipped in logic (oldEmail.equals(newEmail))
        // So we do NOT stub existsByEmailPort here to avoid UnnecessaryStubbingException
        
        UpdateProfileCommand command = new UpdateProfileCommand(id, newName, email, bio);
        
        updateProfileService.execute(command);

        verify(loadProfilePort).findById(any(ProfileId.class));
        verify(saveProfilePort).save(any(Profile.class));
        verify(existsByEmailPort, never()).existsByEmail(any());
    }

    @Test
    void should_update_profile_and_check_email_uniqueness_when_email_changes() {
        UUID id = UUID.randomUUID();
        String oldName = "Old Name";
        String newName = "New Name";
        String oldEmail = "old@example.com";
        String newEmail = "new@example.com";
        String bio = "Bio";

        Profile profile = new Profile(new ProfileId(id), new Name(oldName), new Email(oldEmail), new Bio(bio));
        
        when(loadProfilePort.findById(any(ProfileId.class))).thenReturn(Optional.of(profile));
        when(existsByEmailPort.existsByEmail(newEmail)).thenReturn(false);

        UpdateProfileCommand command = new UpdateProfileCommand(id, newName, newEmail, bio);
        
        updateProfileService.execute(command);

        verify(loadProfilePort).findById(any(ProfileId.class));
        verify(existsByEmailPort).existsByEmail(newEmail);
        verify(saveProfilePort).save(any(Profile.class));
    }

    @Test
    void should_throw_duplicate_exception_when_new_email_exists() {
        UUID id = UUID.randomUUID();
        String oldEmail = "old@test.com";
        String conflictingEmail = "conflict@example.com";

        Profile targetProfile = new Profile(new ProfileId(id), new Name("Target"), new Email(oldEmail), new Bio());
        
        when(loadProfilePort.findById(any(ProfileId.class))).thenReturn(Optional.of(targetProfile));
        when(existsByEmailPort.existsByEmail(conflictingEmail)).thenReturn(true);

        UpdateProfileCommand command = new UpdateProfileCommand(id, "New", conflictingEmail, "");
        
        assertThatThrownBy(() -> updateProfileService.execute(command))
            .isInstanceOf(DuplicateEmailException.class);
            
        verify(saveProfilePort, never()).save(any());
    }

    @Test
    void should_throw_exception_when_not_found() {
        UUID id = UUID.randomUUID();
        
        when(loadProfilePort.findById(any(ProfileId.class))).thenReturn(Optional.empty());

        UpdateProfileCommand command = new UpdateProfileCommand(id, "New", "new@test.com", "");

        assertThatThrownBy(() -> updateProfileService.execute(command))
            .isInstanceOf(ProfileNotFoundException.class)
            .hasMessageContaining("profile not found");
            
        verify(saveProfilePort, never()).save(any());
    }
}

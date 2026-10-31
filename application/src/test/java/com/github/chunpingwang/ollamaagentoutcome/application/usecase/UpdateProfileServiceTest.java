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

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileCommand;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.profile.Bio;
import com.github.chunpingwang.ollamaagentoutcome.domain.profile.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.profile.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.profile.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.profile.exception.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.profile.exception.ProfileNotFoundException;

@ExtendWith(MockitoExtension.class)
class UpdateProfileServiceTest {

    @Mock
    private LoadProfilePort loadProfilePort;

    @Mock
    private SaveProfilePort saveProfilePort;

    @InjectMocks
    private UpdateProfileService updateProfileService;

    @Test
    void should_update_profile_when_exists_and_email_is_unique() {
        // given
        UUID id = UUID.randomUUID();
        String currentEmail = "old@example.com";
        String newDisplayName = "New Name";
        String newBio = "Updated Bio";
        
        Profile existingProfile = new Profile(
            id, 
            Email.of(currentEmail), 
            DisplayName.of("Old Name"), 
            Bio.of(null)
        );
        
        when(loadProfilePort.findById(any())).thenReturn(Optional.of(existingProfile));
        when(saveProfilePort.save(any(Profile.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        UpdateProfileCommand command = new UpdateProfileCommand(new com.github.chunpingwang.ollamaagentoutcome.domain.profile.ProfileId(id), currentEmail, newDisplayName, newBio);

        // when
        updateProfileService.execute(command);

        // then
        verify(loadProfilePort).findById(any());
        assertThat(existingProfile.getDisplayName().name()).isEqualTo(newDisplayName);
        assertThat(existingProfile.getBio().content()).isEqualTo(newBio);
        verify(saveProfilePort).save(existingProfile);
    }

    @Test
    void should_throw_not_found_exception_when_id_does_not_exist() {
        // given
        UUID id = UUID.randomUUID();
        when(loadProfilePort.findById(any())).thenReturn(Optional.empty());
        
        UpdateProfileCommand command = new UpdateProfileCommand(new com.github.chunpingwang.ollamaagentoutcome.domain.profile.ProfileId(id), "email@test.com", "Name", null);

        // when & then
        assertThatThrownBy(() -> updateProfileService.execute(command))
            .isInstanceOf(ProfileNotFoundException.class)
            .hasMessageContaining(id.toString());
            
        verify(saveProfilePort, never()).save(any());
    }

    @Test
    void should_throw_duplicate_email_exception_when_new_email_belongs_to_other_profile() {
        // given
        UUID targetId = UUID.randomUUID();
        String otherUserIdStr = UUID.randomUUID().toString();
        String duplicateEmail = "shared@example.com";
        
        Profile targetProfile = new Profile(
            targetId, 
            Email.of("original@" + targetId.toString()), 
            DisplayName.of("Target"), 
            Bio.of(null)
        );
        
        Profile otherProfile = new Profile(
                java.util.UUID.fromString(otherUserIdStr), 
                Email.of(duplicateEmail), 
                DisplayName.of("Other"), 
                Bio.of(null)
        );
        
        when(loadProfilePort.findById(any())).thenReturn(Optional.of(targetProfile));
        when(loadProfilePort.findByEmail(any())).thenReturn(Optional.of(otherProfile));
        
        UpdateProfileCommand command = new UpdateProfileCommand(new com.github.chunpingwang.ollamaagentoutcome.domain.profile.ProfileId(targetId), duplicateEmail, "New Name", null);

        // when & then
        assertThatThrownBy(() -> updateProfileService.execute(command))
            .isInstanceOf(DuplicateEmailException.class)
            .hasMessageContaining("Duplicate email");
            
        verify(saveProfilePort, never()).save(any());
    }
}

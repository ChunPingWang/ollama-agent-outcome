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
        String newName = "Jane Doe";
        String newEmailValue = "jane@example.com";
        
        Profile existingProfile = new Profile(profileId, "John Doe", new Email("john@example.com"));
        when(loadProfilePort.findById(profileId)).thenReturn(Optional.of(existingProfile));
        
        Profile updatedProfile = new Profile(profileId, newName, new Email(newEmailValue));
        when(saveProfilePort.save(any(Profile.class))).thenReturn(updatedProfile);

        // When
        updateProfileService.update(id, newName, newEmailValue);

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
        assertThatThrownBy(() -> updateProfileService.update(id, "New Name", "new@example.com"))
                .isInstanceOf(ProfileNotFoundException.class)
                .hasMessageContaining(id.toString());
    }

    @Test
    void should_throw_duplicate_email_exception() {
        // Given
        UUID id = UUID.randomUUID();
        ProfileId profileId = new ProfileId(id);
        String duplicateEmailValue = "duplicate@example.com";
        
        Profile existingProfile = new Profile(profileId, "John Doe", new Email("john@example.com"));
        when(loadProfilePort.findById(profileId)).thenReturn(Optional.of(existingProfile));
        
        when(saveProfilePort.save(any(Profile.class)))
                .thenThrow(new DuplicateEmailException(new Email(duplicateEmailValue)));

        // When & Then
        assertThatThrownBy(() -> updateProfileService.update(id, "New Name", duplicateEmailValue))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining(duplicateEmailValue);
    }
}

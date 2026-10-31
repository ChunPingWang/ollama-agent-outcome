package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;

import java.util.Optional;
import java.util.UUID;

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
    void should_throw_profile_not_found_when_load_returns_empty() {
        ProfileId id = new ProfileId(UUID.randomUUID());
        
        when(loadProfilePort.findById(id)).thenReturn(Optional.empty());

        Email email = new Email("new@example.com");
        DisplayName displayName = new DisplayName("New Name");

        assertThatThrownBy(() -> updateProfileService.execute(id, email, displayName))
                .isInstanceOf(ProfileNotFoundException.class)
                .hasMessageContaining("not found");
          
        verify(loadProfilePort).findById(id);
        verify(saveProfilePort, never()).save(any());
    }

    @Test
    void should_save_updated_profile_successfully() {
        ProfileId id = new ProfileId(UUID.randomUUID());
        Profile existingProfile = new Profile(
                id, 
                new Email("old@example.com"), 
                new DisplayName("Old Name")
        );
        
        when(loadProfilePort.findById(id)).thenReturn(Optional.of(existingProfile));
        when(saveProfilePort.save(any(Profile.class))).thenReturn(existingProfile);

        Email newEmail = new Email("updated@example.com");
        DisplayName newName = new DisplayName("Updated Name");

        updateProfileService.execute(id, newEmail, newName);
        
        verify(loadProfilePort).findById(id);
        verify(saveProfilePort).save(any());
    }

    @Test
    void should_throw_duplicate_email_exception_when_save_fails_with_duplicate() {
        ProfileId id = new ProfileId(UUID.randomUUID());
        Profile existingProfile = new Profile(
                id, 
                new Email("original@test.com"), 
                new DisplayName("Original")
        );
        
        when(loadProfilePort.findById(id)).thenReturn(Optional.of(existingProfile));
        when(saveProfilePort.save(any(Profile.class)))
                .thenThrow(new DuplicateEmailException("Duplicate email"));

        assertThatThrownBy(() -> updateProfileService.execute(id, new Email("dup@test.com"), new DisplayName("Name")))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("Duplicate email");
          
        verify(loadProfilePort).findById(id);
        verify(saveProfilePort).save(any());
    }
}

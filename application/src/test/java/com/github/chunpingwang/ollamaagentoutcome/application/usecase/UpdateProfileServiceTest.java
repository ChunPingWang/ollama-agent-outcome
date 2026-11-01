package com.github.chunpingwang.ollamaagentoutcome.application.usecase;




import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
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
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByIdPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class UpdateProfileServiceTest {

    @Mock
    private FindProfileByIdPort findProfileByIdPort;

    @Mock
    private SaveProfilePort saveProfilePort;

    @InjectMocks
    private UpdateProfileService updateProfileService;

    @Test
    void should_update_profile_successfully() {
        // given
        UUID id = UUID.randomUUID();
        String newEmail = "new@example.com";
        String newName = "New Name";
        
        Profile existingProfile = new Profile(
            new ProfileId(id), 
            new Email("old@example.com"), 
            new DisplayName("Old Name")
        );
        
        when(findProfileByIdPort.findById(id)).thenReturn(Optional.of(existingProfile));
        doNothing().when(saveProfilePort).save(any(Profile.class));

        // when & then
        assertThatNoException().isThrownBy(() -> 
            updateProfileService.execute(new UpdateProfileCommand(id, newEmail, newName))
        );
        
        verify(saveProfilePort).save(argThat(profile -> 
            profile.email().value().equals(newEmail) && 
            profile.displayName().value().equals(newName)
        ));
    }

    @Test
    void should_throw_exception_when_profile_not_found_for_update() {
        // given
        UUID id = UUID.randomUUID();
        when(findProfileByIdPort.findById(id)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> 
            updateProfileService.execute(new UpdateProfileCommand(id, "test@test.com", "Name"))
        ).isInstanceOf(ProfileNotFoundException.class);
         
        verifyNoInteractions(saveProfilePort);
    }

    @Test
    void should_propagate_duplicate_email_exception_on_update() {
        // given
        UUID id = UUID.randomUUID();
        String duplicateEmail = "duplicate@example.com";
        String newName = "New Name";
        
        Profile existingProfile = new Profile(
            new ProfileId(id), 
            new Email("original@example.com"), 
            new DisplayName("Original")
        );
        
        when(findProfileByIdPort.findById(id)).thenReturn(Optional.of(existingProfile));
        doThrow(new DuplicateEmailException("email already exists"))
            .when(saveProfilePort).save(any(Profile.class));

        // when & then
        assertThatThrownBy(() -> 
            updateProfileService.execute(new UpdateProfileCommand(id, duplicateEmail, newName))
        ).isInstanceOf(DuplicateEmailException.class)
         .hasMessageContaining("already exists");
    }
}

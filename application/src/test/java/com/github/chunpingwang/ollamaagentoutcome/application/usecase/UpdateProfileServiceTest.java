package com.github.chunpingwang.ollamaagentoutcome.application.usecase;



import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;

/**
 * UpdateProfileService 的單元測試。
 */
@MockitoSettings(strictness = Strictness.LENIENT)
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
        // Arrange
        UUID id = UUID.randomUUID();
        Profile existingProfile = new Profile(
            new ProfileId(id), 
            new Email("old@example.com"), 
            new DisplayName("Old Name")
        );
        
        when(loadProfilePort.findById(new ProfileId(id))).thenReturn(Optional.of(existingProfile));
        // 修正：save 方法有回傳值，不可使用 doNothing()，應改用 when().thenReturn()
        when(saveProfilePort.save(any(Profile.class))).thenReturn(existingProfile);

        // Act & Assert
        updateProfileService.execute(
            new ProfileId(id), 
            new Email("new@example.com"), 
            new DisplayName("New Name")
        );
        
        verify(loadProfilePort).findById(new ProfileId(id));
        verify(saveProfilePort).save(argThat(profile -> profile.getId().id().equals(id)));
    }

    @Test
    void should_throw_exception_when_profile_not_found() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(loadProfilePort.findById(new ProfileId(id))).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> 
            updateProfileService.execute(
                new ProfileId(id), 
                new Email("test@test.com"), 
                new DisplayName("Name")
            )
        ).isInstanceOf(ProfileNotFoundException.class)
         .hasMessageContaining(id.toString());
         
        verifyNoInteractions(saveProfilePort);
    }

    @Test
    void should_propagate_duplicate_email_exception_from_save_port() {
        // Arrange
        UUID id = UUID.randomUUID();
        Profile existingProfile = new Profile(
            new ProfileId(id), 
            new Email("old@example.com"), 
            new DisplayName("Old Name")
        );
        
        when(loadProfilePort.findById(new ProfileId(id))).thenReturn(Optional.of(existingProfile));
        doThrow(new DuplicateEmailException("email already exists"))
            .when(saveProfilePort).save(any(Profile.class));

        // Act & Assert
        assertThatThrownBy(() -> 
            updateProfileService.execute(
                new ProfileId(id), 
                new Email("duplicate@test.com"), 
                new DisplayName("New Name")
            )
        ).isInstanceOf(DuplicateEmailException.class)
         .hasMessageContaining("email already exists");
         
        verify(loadProfilePort).findById(new ProfileId(id));
        verify(saveProfilePort).save(argThat(profile -> profile.getId().id().equals(id)));
    }
}

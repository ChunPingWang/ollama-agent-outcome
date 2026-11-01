package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;

@ExtendWith(MockitoExtension.class)
class UpdateProfileServiceTest {

    private final UUID testId = UUID.randomUUID();
    
    @Mock
    private LoadProfilePort loadProfilePort;
    
    @Mock
    private SaveProfilePort saveProfilePort;

    @InjectMocks
    private UpdateProfileService updateProfileService;

    @Test
    void should_update_profile_when_exists() {
        Profile existingProfile = new Profile(
            new ProfileId(testId), 
            new Email("old@example.com"), 
            new DisplayName("Old Name")
        );
        
        when(loadProfilePort.findById(new ProfileId(testId))).thenReturn(Optional.of(existingProfile));

        String newEmail = "new@example.com";
        String newName = "New Name";

        updateProfileService.execute(testId, newEmail, newName);

        verify(saveProfilePort).save(org.mockito.ArgumentMatchers.any(Profile.class));
    }

    @Test
    void should_throw_not_found_exception_when_profile_does_not_exist() {
        when(loadProfilePort.findById(new ProfileId(testId))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> 
            updateProfileService.execute(testId, "test@test.com", "User")
        ).isInstanceOf(ProfileNotFoundException.class)
         .hasMessageContaining(testId.toString());
         
        // 驗證因為找不到資料，所以不會呼叫 save
        verify(saveProfilePort, org.mockito.Mockito.never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void should_throw_duplicate_email_exception_when_save_fails_with_conflict() {
        Profile existingProfile = new Profile(
            new ProfileId(testId), 
            new Email("original@test.com"), 
            new DisplayName("Original")
        );
        
        when(loadProfilePort.findById(new ProfileId(testId))).thenReturn(Optional.of(existingProfile));
        
        doThrow(new DuplicateEmailException("duplicate"))
                .when(saveProfilePort)
                .save(org.mockito.ArgumentMatchers.any());

        assertThatThrownBy(() -> 
            updateProfileService.execute(testId, "dup@test.com", "New Name")
        ).isInstanceOf(DuplicateEmailException.class)
         .hasMessageContaining("duplicate");
         
        // 驗證例外發生時，load 和 save 確實被呼叫過
        verify(loadProfilePort).findById(new ProfileId(testId));
        verify(saveProfilePort).save(org.mockito.ArgumentMatchers.any());
    }
}

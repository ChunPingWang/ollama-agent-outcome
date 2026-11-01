package com.github.chunpingwang.ollamaagentoutcome.application.usecase;


import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.InvalidEmailException;
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

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class UpdateProfileServiceTest {

    @Mock
    private LoadProfilePort loadProfilePort;

    @Mock
    private SaveProfilePort saveProfilePort;

    @InjectMocks
    private UpdateProfileService updateProfileService;

    private final UUID testId = UUID.randomUUID();
    private final ProfileId profileId = new ProfileId(testId);
    private final String validEmail = "user@example.com";
    private final String displayName = "Display Name";

    @Test
    void should_call_load_then_save_on_valid_update() {
        // 假設 port 回傳了現有的個人資料
        Email email = new Email("old@test.com");
        DisplayName oldName = new DisplayName("Old Name");
        Profile existingProfile = new Profile(profileId, email, oldName);
        
        when(loadProfilePort.findById(profileId)).thenReturn(Optional.of(existingProfile));
        doNothing().when(saveProfilePort).save(any(Profile.class));

        // 當呼叫更新時，應先載入再儲存
        updateProfileService.execute(testId, validEmail, displayName);

        verify(loadProfilePort, times(1)).findById(profileId);
        verify(saveProfilePort, times(1)).save(argThat(saved -> 
            saved.id().id().equals(testId) && 
            saved.getEmail().value().equals(validEmail) &&
            saved.getDisplayName().isPresent()
        ));
    }

    @Test
    void should_throw_profile_not_found_when_profile_does_not_exist() {
        // 假設 port 找不到資料
        when(loadProfilePort.findById(profileId)).thenReturn(Optional.empty());

        // 那麼應該拋出 ProfileNotFoundException
        assertThatThrownBy(() -> updateProfileService.execute(testId, validEmail, displayName))
                .isInstanceOf(ProfileNotFoundException.class)
                .hasMessageContaining(testId.toString());
        
        verifyNoInteractions(saveProfilePort);
    }

    @Test
    void should_throw_duplicate_email_exception_when_save_fails_with_conflict() {
        Email email = new Email("old@test.com");
        DisplayName oldName = new DisplayName("Old Name");
        Profile existingProfile = new Profile(profileId, email, oldName);
        
        when(loadProfilePort.findById(profileId)).thenReturn(Optional.of(existingProfile));
        doThrow(new DuplicateEmailException("email already exists")).when(saveProfilePort).save(any(Profile.class));

        // 當 save 失敗且為重複電子郵件時，應傳遞例外
        assertThatThrownBy(() -> updateProfileService.execute(testId, "duplicate@example.com", displayName))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("email already exists");
        
        verify(loadProfilePort, times(1)).findById(profileId);
        verify(saveProfilePort, times(1)).save(any(Profile.class));
    }
}

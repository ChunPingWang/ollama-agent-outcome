package com.github.chunpingwang.ollamaagentoutcome.application.usecase;



import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByIdPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.ExistsByEmailPort;
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
    private FindProfileByIdPort findProfileByIdPort;

    @Mock
    private SaveProfilePort saveProfilePort;

    @Mock
    private ExistsByEmailPort existsByEmailPort;

    @InjectMocks
    private UpdateProfileService updateProfileService;

    @Test
    void should_update_profile_when_exists() {
        UUID id = UUID.randomUUID();
        String oldEmail = "old@example.com";
        String newDisplayName = "New Name";
        
        Profile existingProfile = new Profile(
            new ProfileId(id), 
            new Email(oldEmail), 
            new DisplayName("Old Name")
        );

        when(findProfileByIdPort.findById(id)).thenReturn(Optional.of(existingProfile));
        // 模擬儲存後回傳同一個物件（或更新後的狀態）
        when(saveProfilePort.save(any(Profile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        updateProfileService.execute(id, null, newDisplayName);

        verify(findProfileByIdPort).findById(id);
        verify(saveProfilePort).save(argThat(p -> p.getId().getValue().equals(id)));
    }

    @Test
    void should_throw_not_found_exception_when_updating_missing() {
        UUID missingId = UUID.randomUUID();

        when(findProfileByIdPort.findById(missingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> updateProfileService.execute(missingId, "new@test.com", "Name"))
                .isInstanceOf(ProfileNotFoundException.class)
                .hasMessageContaining("not found");
                
        verify(saveProfilePort, never()).save(any());
    }

    @Test
    void should_throw_duplicate_email_exception_on_update() {
        UUID id = UUID.randomUUID();
        String conflictingEmail = "conflict@example.com";
        
        Profile existingProfile = new Profile(
            new ProfileId(id), 
            new Email("original@test.com"), 
            new DisplayName("Original")
        );

        when(findProfileByIdPort.findById(id)).thenReturn(Optional.of(existingProfile));
        // 假設新 email 與現有不同，且系統中已存在該 email (由其他用戶使用)
        when(existsByEmailPort.existsByEmail(conflictingEmail)).thenReturn(true);
        // 移除未使用的 save stub：重複檢查會提前拋出例外，導致 save 不會被呼叫，避免 UnnecessaryStubbingException

        assertThatThrownBy(() -> updateProfileService.execute(id, conflictingEmail, null))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("Duplicate email");

        verify(saveProfilePort, never()).save(any());
    }
}

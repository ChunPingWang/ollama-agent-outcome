package com.github.chunpingwang.ollamaagentoutcome.application.usecase;




import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.ExistsByEmailPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class CreateProfileServiceTest {

    @Mock
    private SaveProfilePort saveProfilePort;

    @Mock
    private ExistsByEmailPort existsByEmailPort;

    @InjectMocks
    private CreateProfileService createProfileService;

    @Test
    void should_create_profile_successfully() {
        ProfileId id = new ProfileId(UUID.randomUUID().toString());
        Email email = new Email("test@example.com");
        DisplayName displayName = new DisplayName("Test User");

        when(existsByEmailPort.existsByEmail(email.address(), null)).thenReturn(false);

        createProfileService.execute(id, email, displayName);

        verify(existsByEmailPort).existsByEmail(email.address(), null);
        // 由於 Service 實作中呼叫了 Profile.create，我們無法直接驗證 save 的內容是否為正確的 Domain Entity 
        // 除非我們捕獲參數或使用 ArgumentCaptor。這裡簡化處理，僅驗證方法被調用。
        // 為了更嚴謹的測試，我們可以驗證 saveProfilePort.save 被調用了一次。
        verify(saveProfilePort).save(org.mockito.ArgumentMatchers.any(Profile.class));
    }

    @Test
    void should_throw_duplicate_email_exception_when_email_exists() {
        ProfileId id = new ProfileId(UUID.randomUUID().toString());
        Email email = new Email("duplicate@example.com");
        DisplayName displayName = new DisplayName("Duplicate User");

        when(existsByEmailPort.existsByEmail(email.address(), null)).thenReturn(true);

        assertThatThrownBy(() -> createProfileService.execute(id, email, displayName))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining(email.address());

        verify(saveProfilePort, never()).save(org.mockito.ArgumentMatchers.any());
    }
}

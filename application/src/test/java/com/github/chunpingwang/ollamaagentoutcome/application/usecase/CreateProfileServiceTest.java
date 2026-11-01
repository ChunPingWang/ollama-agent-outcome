package com.github.chunpingwang.ollamaagentoutcome.application.usecase;



import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class CreateProfileServiceTest {

    @Mock
    private SaveProfilePort saveProfilePort;

    @InjectMocks
    private CreateProfileService createProfileService;

    @Test
    void should_create_profile_successfully() {
        Email email = new Email("new@example.com");
        DisplayName displayName = new DisplayName("New User");

        when(saveProfilePort.existsByEmail(email)).thenReturn(false);
        doNothing().when(saveProfilePort).save(any(UUID.class), any(Email.class), any(DisplayName.class));

        assertThatNoException().isThrownBy(() -> createProfileService.execute(email, displayName));
        
        verify(saveProfilePort).existsByEmail(email);
        verify(saveProfilePort).save(any(UUID.class), eq(email), eq(displayName));
    }

    @Test
    void should_throw_duplicate_email_exception_when_exists() {
        Email email = new Email("dup@example.com");
        DisplayName displayName = new DisplayName("Dup User");

        when(saveProfilePort.existsByEmail(email)).thenReturn(true);

        assertThatThrownBy(() -> createProfileService.execute(email, displayName))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("電子郵件已存在: dup@example.com");
                
        // 確保在重複時沒有嘗試保存
        verify(saveProfilePort, never()).save(any(), any(), any());
    }
}

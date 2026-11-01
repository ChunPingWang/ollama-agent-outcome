package com.github.chunpingwang.ollamaagentoutcome.application.usecase;



import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.ExistsByEmailPort;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateProfileServiceTest {

    @Mock
    private ExistsByEmailPort existsByEmailPort;

    @Mock
    private SaveProfilePort saveProfilePort;

    @InjectMocks
    private CreateProfileService createProfileService;

    @Test
    void should_create_profile_when_email_is_unique() {
        String email = "new@example.com";
        String displayName = "New User";
        UUID generatedId = UUID.randomUUID();

        when(existsByEmailPort.existsByEmail(email)).thenReturn(false);
        
        // 模擬 save 回傳帶有 ID 的 Profile，以便後續驗證呼叫參數
        var savedProfile = new Profile(new ProfileId(generatedId), new Email(email), new DisplayName(displayName));
        when(saveProfilePort.save(any(Profile.class))).thenReturn(savedProfile);

        createProfileService.execute(email, displayName);

        verify(existsByEmailPort).existsByEmail(email);
        verify(saveProfilePort).save(argThat(p -> 
            p.getEmail().getAddress().equals(email) && 
            p.getDisplayName().getName().equals(displayName)));
    }

    @Test
    void should_throw_duplicate_exception_when_email_exists() {
        String email = "existing@example.com";
        String displayName = "Existing User";

        when(existsByEmailPort.existsByEmail(email)).thenReturn(true);

        assertThatThrownBy(() -> createProfileService.execute(email, displayName))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("Duplicate email");

        verify(saveProfilePort, never()).save(any());
    }
}

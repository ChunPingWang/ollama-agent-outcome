package com.github.chunpingwang.ollamaagentoutcome.application.usecase;


import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.InvalidEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
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

    private final UUID testId = UUID.randomUUID();
    private final String validEmail = "user@example.com";
    private final String displayName = "Display Name";

    @Test
    void should_call_save_port_on_valid_input() {
        // 當呼叫建立時，應正確傳遞參數並呼叫 port
        when(saveProfilePort.save(any(Profile.class))).thenReturn(null);

        createProfileService.execute(testId, validEmail, displayName);

        verify(saveProfilePort, times(1)).save(argThat(profile -> 
            profile.id().id().equals(testId) && 
            profile.getEmail().value().equals(validEmail)
        ));
    }

    @Test
    void should_throw_duplicate_email_exception_when_port_throws_it() {
        // 假設資料庫中已存在該 email
        doThrow(new DuplicateEmailException("email already exists")).when(saveProfilePort).save(any(Profile.class));

        // 那麼應該拋出 DuplicateEmailException
        assertThatThrownBy(() -> createProfileService.execute(testId, validEmail, displayName))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("email already exists");
        
        verify(saveProfilePort, times(1)).save(any(Profile.class));
    }

    @Test
    void should_not_call_port_when_email_is_invalid() {
        String invalidEmail = "invalid-email";

        // 因為 Email value object 建構子會驗證格式，若無效會在進入 service 前就拋出例外
        // 因此 saveProfilePort 絕對不會被呼叫
        assertThatThrownBy(() -> createProfileService.execute(testId, invalidEmail, displayName))
                .isInstanceOf(InvalidEmailException.class);

        verifyNoInteractions(saveProfilePort);
    }
}

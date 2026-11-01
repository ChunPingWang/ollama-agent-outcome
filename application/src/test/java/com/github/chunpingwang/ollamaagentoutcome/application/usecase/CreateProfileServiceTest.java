package com.github.chunpingwang.ollamaagentoutcome.application.usecase;



import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.InvalidEmailFormatException;
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
    void should_create_profile_and_call_save_once() {
        String email = "user@example.com";
        String displayName = "Test User";
        
        var request = new CreateProfileUseCase.CreateProfileRequest(email, displayName);
        
        // 執行 Use Case
        createProfileService.execute(request);
        
        // 驗證 save port 被呼叫了一次
        verify(saveProfilePort).save(any());
    }

    @Test
    void should_propagate_duplicate_email_exception() {
        String email = "duplicate@example.com";
        var request = new CreateProfileUseCase.CreateProfileRequest(email, "User");

        doThrow(new DuplicateEmailException("email already exists"))
            .when(saveProfilePort).save(any());

        assertThatThrownBy(() -> createProfileService.execute(request))
            .isInstanceOf(DuplicateEmailException.class)
            .hasMessageContaining("email already exists");
            
        verify(saveProfilePort).save(any());
    }
    
    @Test
    void should_throw_invalid_email_format_on_bad_input() {
        String badEmail = "invalid-email";
        var request = new CreateProfileUseCase.CreateProfileRequest(badEmail, "User");

        assertThatThrownBy(() -> createProfileService.execute(request))
            .isInstanceOf(InvalidEmailFormatException.class);
            
        // save port 不應該被呼叫，因為在建立 Profile 實體時就會拋出異常
        verify(saveProfilePort, org.mockito.Mockito.never()).save(any());
    }
}

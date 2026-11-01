package com.github.chunpingwang.ollamaagentoutcome.application.usecase;



import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase.CreateProfileRequest;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.InvalidEmailFormatException;

@ExtendWith(MockitoExtension.class)
class CreateProfileServiceTest {

    @Mock
    private SaveProfilePort savePort;

    @InjectMocks
    private CreateProfileService createProfileService;

    @Test
    void should_create_profile_and_call_save_port() {
        var request = new CreateProfileRequest("test@example.com", "Tester");
        
        createProfileService.execute(request);
        
        verify(savePort).save(any());
    }

    @Test
    void should_throw_when_email_format_is_invalid() {
        var request = new CreateProfileRequest("invalid-email", "Tester");
        
        assertThatThrownBy(() -> createProfileService.execute(request))
            .isInstanceOf(InvalidEmailFormatException.class)
            .hasMessageContaining("格式無效");
    }
}

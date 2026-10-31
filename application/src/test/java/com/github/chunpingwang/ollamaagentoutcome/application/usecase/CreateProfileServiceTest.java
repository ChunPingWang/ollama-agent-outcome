package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileCommand;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

@ExtendWith(MockitoExtension.class)
class CreateProfileServiceTest {

    @Mock
    private SaveProfilePort saveProfilePort;

    @InjectMocks
    private CreateProfileService createProfileService;

    @Test
    void should_call_save_port_on_success() {
        var command = new CreateProfileCommand("Alice", "alice@example.com");
        
        // 執行時應無異常，並驗證 saveProfilePort.save() 被呼叫一次。
        createProfileService.execute(command);
        
        verify(saveProfilePort).save(any());
    }

    @Test
    void should_propagate_duplicate_email_exception() {
        var command = new CreateProfileCommand("Bob", "bob@example.com");
        
        doThrow(new DuplicateEmailException("email already exists"))
                .when(saveProfilePort).save(any(Profile.class));

        assertThatThrownBy(() -> createProfileService.execute(command))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("email already exists");
    }
}

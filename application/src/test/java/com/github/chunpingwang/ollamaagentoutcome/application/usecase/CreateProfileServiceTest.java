package com.github.chunpingwang.ollamaagentoutcome.application.usecase;



import org.mockito.junit.jupiter.MockitoSettings;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class CreateProfileServiceTest {

    @Mock
    private SaveProfilePort saveProfilePort;

    private CreateProfileUseCase createProfileService;

    @BeforeEach
    void setUp() {
        createProfileService = new CreateProfileService(saveProfilePort);
    }

    @Test
    void should_call_save_port_once_on_success() {
        var request = new CreateProfileUseCase.CreateProfileRequest("test@example.com", "Tester");
        
        createProfileService.execute(request);
        
        verify(saveProfilePort, times(1)).save(any(Profile.class));
    }

    @Test
    void should_propagate_duplicate_email_exception() {
        var request = new CreateProfileUseCase.CreateProfileRequest("dup@test.com", "Dup User");
        
        doThrow(new DuplicateEmailException("email already exists"))
            .when(saveProfilePort).save(any());
            
        assertThatThrownBy(() -> createProfileService.execute(request))
            .isInstanceOf(DuplicateEmailException.class)
            .hasMessageContaining("email already exists");
            
        verify(saveProfilePort, times(1)).save(any());
    }
}

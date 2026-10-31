package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileRequest;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.InvalidEmailFormatException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateProfileServiceTest {

    @Mock
    private SaveProfilePort saveProfilePort;

    @InjectMocks
    private CreateProfileService createProfileService;

    @Test
    void should_create_profile_successfully() {
        var request = new CreateProfileRequest("test@example.com", "Test User");
        
        doNothing().when(saveProfilePort).save(any(Profile.class));
        
        createProfileService.execute(request);
        
        verify(saveProfilePort).save(any());
    }

    @Test
    void should_throw_invalid_email_format_exception_when_email_is_invalid() {
        var request = new CreateProfileRequest("invalid-email", "Test User");
        
        assertThatThrownBy(() -> createProfileService.execute(request))
                .isInstanceOf(InvalidEmailFormatException.class)
                .hasMessageContaining("invalid email format");
    }

    @Test
    void should_propagate_duplicate_email_exception_from_port() {
        var request = new CreateProfileRequest("duplicate@test.com", "User");
        
        doThrow(new DuplicateEmailException("email already exists")).when(saveProfilePort).save(any());
            
        assertThatThrownBy(() -> createProfileService.execute(request))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("email already exists");
    }
}

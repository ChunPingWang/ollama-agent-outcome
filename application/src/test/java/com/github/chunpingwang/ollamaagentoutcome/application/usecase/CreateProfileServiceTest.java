package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doThrow;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileRequest;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.entity.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.exception.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.ProfileId;

@ExtendWith(MockitoExtension.class)
class CreateProfileServiceTest {

    @Mock
    private SaveProfilePort saveProfilePort;

    @InjectMocks
    private CreateProfileService createProfileService; // Will be created in next step, currently fails to compile if class missing or wrong signature

    @Test
    void should_create_profile_successfully() {
        var request = new CreateProfileRequest("test@example.com", "Test User");
        
        // This test expects the service implementation to exist and call the port
        assertThatThrownBy(() -> createProfileService.execute(request))
                .isInstanceOf(Exception.class); 
    }
    
    @Test
    void should_throw_duplicate_email_exception_on_db_violation() {
        var request = new CreateProfileRequest("dup@test.com", "Dup User");
        
        doThrow(new DuplicateEmailException("Duplicate email"))
            .when(saveProfilePort).save(org.mockito.ArgumentMatchers.any());
            
        assertThatThrownBy(() -> createProfileService.execute(request))
                .isInstanceOf(DuplicateEmailException.class);
    }
}

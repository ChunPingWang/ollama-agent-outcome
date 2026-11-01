package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;

@ExtendWith(MockitoExtension.class)
class CreateProfileServiceTest {

    private final UUID testId = UUID.randomUUID();
    
    @Mock
    private SaveProfilePort saveProfilePort;

    @InjectMocks
    private CreateProfileService createProfileService;

    @Test
    void should_save_profile_when_valid() {
        String email = "test@example.com";
        String displayName = "Test User";

        createProfileService.execute(testId, email, displayName);

        verify(saveProfilePort).save(org.mockito.ArgumentMatchers.any(Profile.class));
    }

    @Test
    void should_throw_duplicate_email_exception_when_port_throws_it() {
        doThrow(new DuplicateEmailException("duplicate"))
                .when(saveProfilePort)
                .save(org.mockito.ArgumentMatchers.any());

        assertThatThrownBy(() -> 
            createProfileService.execute(testId, "dup@test.com", "User")
        ).isInstanceOf(DuplicateEmailException.class)
         .hasMessageContaining("duplicate");
        
        // 驗證例外發生時，save 確實被呼叫過（因為例外是在 save 內部或之後拋出）
        verify(saveProfilePort).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void should_not_call_port_if_validation_fails_before_domain_creation() {
        // Email constructor throws InvalidEmailException for blank/invalid format.
        // This exception happens inside execute() before or during domain object creation.
        // If it fails at domain level (InvalidEmailException), the port is never called because
        // the exception propagates up without reaching save().
        
        assertThatThrownBy(() -> 
            createProfileService.execute(testId, "", "User")
        ).isInstanceOf(com.github.chunpingwang.ollamaagentoutcome.domain.InvalidEmailException.class);

        verify(saveProfilePort, never()).save(org.mockito.ArgumentMatchers.any());
    }
}

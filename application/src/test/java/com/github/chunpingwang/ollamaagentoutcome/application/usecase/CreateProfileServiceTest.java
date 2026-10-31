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

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.InvalidEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.InvalidNameException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase.CreateProfileRequest;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;

@ExtendWith(MockitoExtension.class)
class CreateProfileServiceTest {

    @Mock
    private SaveProfilePort saveProfilePort;

    @InjectMocks
    private CreateProfileService createProfileService;

    @Test
    void should_create_profile_successfully() {
        String name = "王小明";
        String email = "test@example.com";
        CreateProfileRequest request = new CreateProfileRequest(name, email);

        createProfileService.execute(request);

        verify(saveProfilePort).save(any());
    }

    @Test
    void should_throw_invalid_email_exception_when_email_is_invalid() {
        CreateProfileRequest request = new CreateProfileRequest("Valid Name", "invalid-email");

        assertThatThrownBy(() -> createProfileService.execute(request))
                .isInstanceOf(InvalidEmailException.class);
        
        verify(saveProfilePort, org.mockito.Mockito.never()).save(any());
    }

    @Test
    void should_throw_duplicate_email_exception_on_save_failure() {
        String name = "王小明";
        String email = "duplicate@test.com";
        CreateProfileRequest request = new CreateProfileRequest(name, email);

        doThrow(new DuplicateEmailException())
                .when(saveProfilePort).save(any());

        assertThatThrownBy(() -> createProfileService.execute(request))
                .isInstanceOf(DuplicateEmailException.class);
    }
}

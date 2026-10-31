package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.InvalidDisplayNameException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

@ExtendWith(MockitoExtension.class)
class CreateProfileServiceTest {

    @Mock
    private SaveProfilePort saveProfilePort;

    @InjectMocks
    private CreateProfileService createProfileService;

    @Test
    void should_create_profile_successfully() {
        var request = new CreateProfileUseCase.CreateProfileRequest("test@example.com", "John Doe");

        when(saveProfilePort.save(any(Profile.class)))
                .thenReturn(new Profile(
                        new ProfileId(UUID.randomUUID().toString()), 
                        new Email(request.email()), 
                        new com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName(request.displayName())
                ));

        createProfileService.create(request);

        verify(saveProfilePort).save(org.mockito.ArgumentMatchers.argThat(p -> 
            p.email().address().equals("test@example.com") && 
            p.displayName().name().equals("John Doe")
        ));
    }

    @Test
    void should_throw_when_duplicate_email_exists() {
        var request = new CreateProfileUseCase.CreateProfileRequest("duplicate@test.com", "User");
        
        doThrow(new DuplicateEmailException("電子郵件已存在"))
                .when(saveProfilePort).save(any());

        assertThatThrownBy(() -> createProfileService.create(request))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("電子郵件已存在");
    }
}

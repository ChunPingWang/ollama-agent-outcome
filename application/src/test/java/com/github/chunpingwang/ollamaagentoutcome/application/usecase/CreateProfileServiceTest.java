package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.InvalidEmailFormatException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileRequest;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;

@ExtendWith(MockitoExtension.class)
class CreateProfileServiceTest {

    @Mock
    private SaveProfilePort saveProfilePort;

    @InjectMocks
    private CreateProfileService createProfileService;

    @Test
    void should_create_profile_successfully() {
        // given
        String email = "user@example.com";
        String displayName = "John Doe";
        CreateProfileRequest request = new CreateProfileRequest(email, displayName);

        // when
        createProfileService.execute(request);

        // then
        verify(saveProfilePort).save(any(Profile.class));
    }

    @Test
    void should_throw_duplicate_email_exception_when_save_fails_with_duplicate() {
        // given
        doThrow(new DuplicateEmailException("Email already exists"))
            .when(saveProfilePort).save(any(Profile.class));

        // when & then
        assertThatThrownBy(() -> createProfileService.execute(new CreateProfileRequest("duplicate@test.com", "Duplicate User")))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("Email already exists");
    }
    
    @Test
    void should_throw_invalid_email_format_on_bad_input() {
        // given
        String invalidEmail = "not-an-email";

        // when & then
        assertThatThrownBy(() -> createProfileService.execute(new CreateProfileRequest(invalidEmail, "Bad Email User")))
                .isInstanceOf(InvalidEmailFormatException.class);
    }
}

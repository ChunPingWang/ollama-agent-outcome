package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.Bio;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.service.DuplicateEmailException;

@ExtendWith(MockitoExtension.class)
class CreateProfileServiceTest {

    @Mock
    private SaveProfilePort saveProfilePort;

    @InjectMocks
    private CreateProfileService createProfileService;

    @Test
    void should_create_profile_successfully() {
        // Given
        String email = "test@example.com";
        String displayName = "Tester";
        String bio = "Hello World";
        
        doNothing().when(saveProfilePort).save(any(Profile.class));

        // When
        createProfileService.execute(email, displayName, bio);

        // Then
        verify(saveProfilePort).save(any(Profile.class));
    }

    @Test
    void should_throw_duplicate_email_exception_when_save_fails() {
        // Given
        String email = "duplicate@test.com";
        String displayName = "Duplicate User";
        String bio = "Bio text";
        
        doThrow(new DuplicateEmailException("email already exists: " + email))
            .when(saveProfilePort).save(any(Profile.class));

        // When & Then
        assertThatThrownBy(() -> createProfileService.execute(email, displayName, bio))
            .isInstanceOf(DuplicateEmailException.class)
            .hasMessageContaining("email already exists");
    }
}

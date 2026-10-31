package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.exception.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;

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
    void should_create_profile_and_return_id() {
        // Given
        String name = "John Doe";
        String emailValue = "john@example.com";
        UUID generatedId = UUID.randomUUID();
        ProfileId expectedId = new ProfileId(generatedId);
        
        Profile savedProfile = new Profile(expectedId, name, new Email(emailValue));
        when(saveProfilePort.save(any(Profile.class))).thenReturn(savedProfile);

        // When
        ProfileId resultId = createProfileService.create(name, emailValue);

        // Then
        assertThat(resultId).isEqualTo(expectedId);
        verify(saveProfilePort).save(any(Profile.class));
    }

    @Test
    void should_throw_duplicate_email_exception() {
        // Given
        String name = "John Doe";
        String emailValue = "john@example.com";
        
        when(saveProfilePort.save(any(Profile.class)))
                .thenThrow(new DuplicateEmailException(new Email(emailValue)));

        // When & Then
        assertThatThrownBy(() -> createProfileService.create(name, emailValue))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining(emailValue);
    }
}

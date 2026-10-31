package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase.CreateProfileRequest;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByEmailPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.InvalidEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.Age;

@ExtendWith(MockitoExtension.class)
class CreateProfileServiceTest {

    @Mock
    private SaveProfilePort saveProfilePort;

    @Mock
    private FindProfileByEmailPort findProfileByEmailPort;

    @InjectMocks
    private CreateProfileService createProfileService;

    @Test
    void should_create_profile_successfully() {
        // Given
        CreateProfileRequest request = new CreateProfileRequest("user@example.com", "John Doe", 25);
        ProfileId expectedId = new ProfileId(java.util.UUID.randomUUID());
        Email email = new Email("user@example.com");
        Name name = new Name("John Doe");
        Age age = new Age(25);
        Profile expectedProfile = new Profile(expectedId, email, name, age);

        when(findProfileByEmailPort.findByEmail(email)).thenReturn(Optional.empty());
        when(saveProfilePort.save(any(Profile.class))).thenReturn(expectedProfile);

        // When
        ProfileId resultId = createProfileService.create(request);

        // Then
        assertThat(resultId).isEqualTo(expectedId);
        verify(saveProfilePort).save(any(Profile.class));
    }

    @Test
    void should_throw_exception_when_email_already_exists() {
        // Given
        CreateProfileRequest request = new CreateProfileRequest("duplicate@example.com", "John Doe", 25);
        Email email = new Email("duplicate@example.com");
        Profile existingProfile = new Profile(new ProfileId(java.util.UUID.randomUUID()), email, new Name("John"), new Age(25));

        when(findProfileByEmailPort.findByEmail(email)).thenReturn(Optional.of(existingProfile));

        // When & Then
        assertThatThrownBy(() -> createProfileService.create(request))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("Email already exists");

        verify(saveProfilePort, never()).save(any(Profile.class));
    }

    @Test
    void should_throw_exception_when_email_format_is_invalid() {
        // Given
        CreateProfileRequest request = new CreateProfileRequest("invalid-email", "John Doe", 25);

        // When & Then
        assertThatThrownBy(() -> createProfileService.create(request))
                .isInstanceOf(InvalidEmailException.class)
                .hasMessageContaining("Invalid email format");

        verify(findProfileByEmailPort, never()).findByEmail(any());
        verify(saveProfilePort, never()).save(any(Profile.class));
    }
}

package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
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

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileRequest;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

@ExtendWith(MockitoExtension.class)
class CreateProfileServiceTest {

    @Mock
    private SaveProfilePort saveProfilePort;

    @InjectMocks
    private CreateProfileService createProfileService;

    @Test
    void should_create_profile_and_return_id() {
        // Given
        UUID uuid = UUID.randomUUID();
        ProfileId expectedId = new ProfileId(uuid);
        CreateProfileRequest request = new CreateProfileRequest("王小明", "xiaoming@example.com", "測試");
        
        Profile createdProfile = new Profile(expectedId, "王小明", "xiaoming@example.com", "測試");
        when(saveProfilePort.save(any(Profile.class))).thenReturn(createdProfile);

        // When
        ProfileId resultId = createProfileService.execute(request);

        // Then
        assertThat(resultId).isEqualTo(expectedId);
        verify(saveProfilePort).save(any(Profile.class));
    }

    @Test
    void should_throw_duplicate_email_exception_when_email_exists() {
        // Given
        CreateProfileRequest request = new CreateProfileRequest("李大明", "duplicate@example.com", "重複測試");
        doThrow(new DuplicateEmailException("Email already exists")).when(saveProfilePort).save(any(Profile.class));

        // When & Then
        assertThatThrownBy(() -> createProfileService.execute(request))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("Email already exists");
    }
}

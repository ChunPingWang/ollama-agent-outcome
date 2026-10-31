package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.InvalidEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;

import org.junit.jupiter.api.Test;

/**
 * {@link CreateProfileService} 的單元測試。
 * 驗證建立個人資料的業務邏輯，包含成功建立、重複 Email 與無效 Email 的處理。
 */
class CreateProfileServiceTest {

    private static final String VALID_NAME = "John Doe";
    private static final String VALID_EMAIL = "user@example.com";
    private static final String INVALID_EMAIL = "invalid-email";
    private static final ProfileId EXPECTED_ID = new ProfileId("550e8400-e29b-41d4-a716-446655440000");

    private final SaveProfilePort saveProfilePort = mock(SaveProfilePort.class);
    private final CreateProfileUseCase createProfileService = new CreateProfileService(saveProfilePort);

    @Test
    void should_create_profile_successfully() {
        // Arrange
        when(saveProfilePort.save(VALID_NAME, VALID_EMAIL)).thenReturn(EXPECTED_ID);

        // Act
        ProfileId resultId = createProfileService.createProfile(VALID_NAME, VALID_EMAIL);

        // Assert
        assertThat(resultId).isEqualTo(EXPECTED_ID);
        verify(saveProfilePort).save(VALID_NAME, VALID_EMAIL);
    }

    @Test
    void should_throw_exception_when_email_is_invalid() {
        // Act & Assert
        assertThatThrownBy(() -> createProfileService.createProfile(VALID_NAME, INVALID_EMAIL))
                .isInstanceOf(InvalidEmailException.class)
                .hasMessageContaining("invalid email format");
        
        // Verify that port was not called because validation happens before port invocation
        verify(saveProfilePort, org.mockito.Mockito.never()).save(anyString(), anyString());
    }

    @Test
    void should_throw_exception_when_email_is_duplicate() {
        // Arrange
        when(saveProfilePort.save(VALID_NAME, VALID_EMAIL)).thenThrow(new DuplicateEmailException("email already exists"));

        // Act & Assert
        assertThatThrownBy(() -> createProfileService.createProfile(VALID_NAME, VALID_EMAIL))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("email already exists");
    }
}

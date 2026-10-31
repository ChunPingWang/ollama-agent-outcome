package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;

import org.junit.jupiter.api.Test;

/**
 * {@link UpdateProfileService} 的單元測試。
 * 驗證更新個人資料的成功、不存在例外與無效姓名例外。
 */
class UpdateProfileServiceTest {

    private static final String VALID_UUID = "550e8400-e29b-41d4-a716-446655440000";
    private static final String VALID_EMAIL = "user@example.com";
    private static final String OLD_NAME = "Old Name";
    private static final String NEW_NAME = "New Name";

    private final LoadProfilePort loadProfilePort = mock(LoadProfilePort.class);
    private final SaveProfilePort saveProfilePort = mock(SaveProfilePort.class);
    private final UpdateProfileUseCase updateProfileService = new UpdateProfileService(loadProfilePort, saveProfilePort);

    @Test
    void should_update_profile_successfully() {
        // Arrange
        ProfileId id = new ProfileId(VALID_UUID);
        Email email = new Email(VALID_EMAIL);
        Profile originalProfile = new Profile(id, email, OLD_NAME);
        
        when(loadProfilePort.findById(id)).thenReturn(Optional.of(originalProfile));
        
        // Act
        Profile result = updateProfileService.updateProfile(id, NEW_NAME);

        // Assert
        assertThat(result.getName()).isEqualTo(NEW_NAME);
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getEmail()).isEqualTo(email);
        
        // Verify that load was called
        verify(loadProfilePort).findById(id);
        // Verify that save was called with correct arguments
        verify(saveProfilePort).save(NEW_NAME, VALID_EMAIL);
    }

    @Test
    void should_throw_exception_when_profile_not_found() {
        // Arrange
        ProfileId id = new ProfileId("non-existent-id");
        when(loadProfilePort.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> updateProfileService.updateProfile(id, NEW_NAME))
                .isInstanceOf(ProfileNotFoundException.class)
                .hasMessageContaining("not found");
        
        verify(loadProfilePort).findById(id);
        // save should not be called if profile is not found
        verify(saveProfilePort, org.mockito.Mockito.never()).save(anyString(), anyString());
    }

    @Test
    void should_throw_exception_when_new_name_is_blank() {
        // Arrange
        ProfileId id = new ProfileId(VALID_UUID);
        Email email = new Email(VALID_EMAIL);
        Profile originalProfile = new Profile(id, email, OLD_NAME);
        
        when(loadProfilePort.findById(id)).thenReturn(Optional.of(originalProfile));

        // Act & Assert
        assertThatThrownBy(() -> updateProfileService.updateProfile(id, "   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("name");
        
        verify(loadProfilePort).findById(id);
        verify(saveProfilePort, org.mockito.Mockito.never()).save(anyString(), anyString());
    }
}

package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;

import org.junit.jupiter.api.Test;

/**
 * {@link DeleteProfileService} 的單元測試。
 * 驗證刪除個人資料的成功與不存在例外。
 */
class DeleteProfileServiceTest {

    private static final String VALID_UUID = "550e8400-e29b-41d4-a716-446655440000";

    private final LoadProfilePort loadProfilePort = mock(LoadProfilePort.class);
    private final DeleteProfilePort deleteProfilePort = mock(DeleteProfilePort.class);
    private final DeleteProfileUseCase deleteProfileService = new DeleteProfileService(loadProfilePort, deleteProfilePort);

    @Test
    void should_delete_profile_successfully() {
        // Arrange
        ProfileId id = new ProfileId(VALID_UUID);
        Email email = new Email("user@example.com");
        Profile profile = new Profile(id, email, "Test Name");
        
        when(loadProfilePort.findById(id)).thenReturn(Optional.of(profile));
        
        // Act
        deleteProfileService.deleteProfile(id);

        // Assert
        verify(loadProfilePort).findById(id);
        verify(deleteProfilePort).deleteById(id);
    }

    @Test
    void should_throw_exception_when_profile_not_found() {
        // Arrange
        ProfileId id = new ProfileId("non-existent-id");
        when(loadProfilePort.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> deleteProfileService.deleteProfile(id))
                .isInstanceOf(ProfileNotFoundException.class)
                .hasMessageContaining("not found");
        
        verify(loadProfilePort).findById(id);
        // delete should not be called if profile is not found
        verify(deleteProfilePort, org.mockito.Mockito.never()).deleteById(any());
    }
}

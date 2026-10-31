package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.LoadProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;

import org.junit.jupiter.api.Test;

/**
 * {@link LoadProfileService} 的單元測試。
 * 驗證查詢個人資料的存在與不存在情況。
 */
class LoadProfileServiceTest {

    private static final String VALID_UUID = "550e8400-e29b-41d4-a716-446655440000";
    private static final String VALID_NAME = "John Doe";
    private static final String VALID_EMAIL = "user@example.com";

    private final LoadProfilePort loadProfilePort = mock(LoadProfilePort.class);
    private final LoadProfileUseCase loadProfileService = new LoadProfileService(loadProfilePort);

    @Test
    void should_load_profile_when_exists() {
        // Arrange
        ProfileId id = new ProfileId(VALID_UUID);
        Email email = new Email(VALID_EMAIL);
        Profile expectedProfile = new Profile(id, email, VALID_NAME);
        when(loadProfilePort.findById(id)).thenReturn(Optional.of(expectedProfile));

        // Act
        Profile result = loadProfileService.loadProfile(id);

        // Assert
        assertThat(result).isEqualTo(expectedProfile);
    }

    @Test
    void should_throw_exception_when_profile_not_found() {
        // Arrange
        ProfileId id = new ProfileId("non-existent-id");
        when(loadProfilePort.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> loadProfileService.loadProfile(id))
                .isInstanceOf(ProfileNotFoundException.class)
                .hasMessageContaining("not found");
    }
}

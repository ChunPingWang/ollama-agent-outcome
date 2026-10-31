package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;

/**
 * 測試 GetProfileService 的業務邏輯。
 * 此測試在 T3 階段預期會編譯失敗，因為 GetProfileService 尚未實作。
 */
@ExtendWith(MockitoExtension.class)
class GetProfileServiceTest {

    @Mock
    private LoadProfilePort loadProfilePort;

    @InjectMocks
    private GetProfileService getProfileService;

    @Test
    void should_return_profile_when_exists() {
        // Given
        UUID id = UUID.randomUUID();
        String name = "張三";
        String email = "zhangsan@example.com";
        Profile expectedProfile = new Profile(new ProfileId(id), name, new Email(email));
        
        GetProfileCommand command = new GetProfileCommand(id);
        when(loadProfilePort.findById(new ProfileId(id))).thenReturn(Optional.of(expectedProfile));

        // When
        Profile result = getProfileService.execute(command);

        // Then
        assertThat(result).isEqualTo(expectedProfile);
        assertThat(result.id()).isEqualTo(new ProfileId(id));
        assertThat(result.name()).isEqualTo(name);
        assertThat(result.email().value()).isEqualTo(email);
    }

    @Test
    void should_throw_profile_not_found_exception_when_not_exists() {
        // Given
        UUID id = UUID.randomUUID();
        GetProfileCommand command = new GetProfileCommand(id);
        when(loadProfilePort.findById(new ProfileId(id))).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> getProfileService.execute(command))
                .isInstanceOf(ProfileNotFoundException.class)
                .hasMessageContaining("not found");
    }
}

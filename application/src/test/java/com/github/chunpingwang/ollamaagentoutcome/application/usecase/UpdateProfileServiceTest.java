package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chunpingwang.ollamaagentoutcome.domain.Bio;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;

@ExtendWith(MockitoExtension.class)
class UpdateProfileServiceTest {

    @Mock
    private LoadProfilePort loadProfilePort;

    @Mock
    private SaveProfilePort saveProfilePort;

    private UpdateProfileUseCase updateProfileService;

    @BeforeEach
    void setUp() {
        updateProfileService = new UpdateProfileService(loadProfilePort, saveProfilePort);
    }

    @Test
    @DisplayName("應能成功更新個人資料")
    void should_update_profile_successfully() {
        String profileId = UUID.randomUUID().toString();
        String oldEmail = "old@test.com";
        String newName = "New Name";
        String newBio = "Updated Bio";

        Profile existingProfile = new Profile(
            new ProfileId(profileId),
            new Email(oldEmail),
            new Name("Old Name"),
            new Bio("Old Bio")
        );

        when(loadProfilePort.findById(profileId)).thenReturn(Optional.of(existingProfile));

        // 執行
        GetProfileUseCase.ProfileResponse response = updateProfileService.update(profileId, new UpdateProfileUseCase.UpdateProfileRequest(oldEmail, newName, newBio));

        // 驗證
        assertThat(response.name()).isEqualTo(newName);
        assertThat(response.bio()).isEqualTo(newBio);
        verify(saveProfilePort).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    @DisplayName("當更新不存在的 ID 時，應拋出 ProfileNotFoundException")
    void should_throw_not_found_exception() {
        String profileId = "non-existent-id";
        String email = "test@test.com";
        String name = "Test";
        String bio = "Test";

        when(loadProfilePort.findById(profileId)).thenReturn(Optional.empty());

        // 執行與驗證
        assertThatThrownBy(() -> updateProfileService.update(profileId, new UpdateProfileUseCase.UpdateProfileRequest(email, name, bio)))
            .isInstanceOf(ProfileNotFoundException.class)
            .hasMessageContaining(profileId);
    }
}

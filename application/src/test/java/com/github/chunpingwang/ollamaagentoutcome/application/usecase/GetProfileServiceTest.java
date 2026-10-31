package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;

@ExtendWith(MockitoExtension.class)
class GetProfileServiceTest {

    @Mock
    private LoadProfilePort loadProfilePort;

    private GetProfileUseCase getProfileService;

    @BeforeEach
    void setUp() {
        getProfileService = new GetProfileService(loadProfilePort);
    }

    @Test
    @DisplayName("應能成功查詢個人資料")
    void should_get_profile_successfully() {
        String profileId = UUID.randomUUID().toString();
        String email = "charlie@example.com";
        String name = "Charlie";
        String bio = "Writer";

        Profile existingProfile = new Profile(
            new ProfileId(profileId),
            new Email(email),
            new Name(name),
            new Bio(bio)
        );

        when(loadProfilePort.findById(profileId)).thenReturn(Optional.of(existingProfile));

        // 執行
        GetProfileUseCase.ProfileResponse response = getProfileService.get(profileId);

        // 驗證
        assertThat(response.id()).isEqualTo(profileId);
        assertThat(response.email()).isEqualTo(email);
        assertThat(response.name()).isEqualTo(name);
        assertThat(response.bio()).isEqualTo(bio);
    }

    @Test
    @DisplayName("當查詢不存在的 ID 時，應拋出 ProfileNotFoundException")
    void should_throw_not_found_exception() {
        String profileId = "non-existent-id";

        when(loadProfilePort.findById(profileId)).thenReturn(Optional.empty());

        // 執行與驗證
        assertThatThrownBy(() -> getProfileService.get(profileId))
            .isInstanceOf(ProfileNotFoundException.class)
            .hasMessageContaining(profileId);
    }
}

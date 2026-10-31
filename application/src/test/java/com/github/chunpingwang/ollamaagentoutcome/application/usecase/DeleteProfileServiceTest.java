package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

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
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;

@ExtendWith(MockitoExtension.class)
class DeleteProfileServiceTest {

    @Mock
    private LoadProfilePort loadProfilePort;

    @Mock
    private DeleteProfilePort deleteProfilePort;

    private DeleteProfileUseCase deleteProfileService;

    @BeforeEach
    void setUp() {
        deleteProfileService = new DeleteProfileService(loadProfilePort, deleteProfilePort);
    }

    @Test
    @DisplayName("應能成功刪除個人資料")
    void should_delete_profile_successfully() {
        String profileId = UUID.randomUUID().toString();

        when(loadProfilePort.findById(profileId)).thenReturn(Optional.of(new Profile(
            new ProfileId(profileId),
            new Email("test@test.com"),
            new Name("Test"),
            new Bio("Test Bio")
        )));

        // 執行
        deleteProfileService.delete(profileId);

        // 驗證
        verify(deleteProfilePort).deleteById(profileId);
    }

    @Test
    @DisplayName("當刪除不存在的 ID 時，應拋出 ProfileNotFoundException")
    void should_throw_not_found_exception() {
        String profileId = "non-existent-id";

        when(loadProfilePort.findById(profileId)).thenReturn(Optional.empty());

        // 執行與驗證
        assertThatThrownBy(() -> deleteProfileService.delete(profileId))
            .isInstanceOf(ProfileNotFoundException.class)
            .hasMessageContaining(profileId);
    }
}

package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;

@ExtendWith(MockitoExtension.class)
class CreateProfileServiceTest {

    @Mock
    private SaveProfilePort saveProfilePort;

    private CreateProfileUseCase createProfileService;

    @BeforeEach
    void setUp() {
        createProfileService = new CreateProfileService(saveProfilePort);
    }

    @Test
    @DisplayName("應能成功建立個人資料並呼叫 Port")
    void should_create_profile_successfully() {
        String email = "alice@example.com";
        String name = "Alice";
        String bio = "Developer";

        // 執行
        createProfileService.create(new CreateProfileUseCase.CreateProfileRequest(email, name, bio));

        // 驗證
        verify(saveProfilePort).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    @DisplayName("當電子郵件重複時，應拋出 DuplicateEmailException")
    void should_throw_duplicate_email_exception() {
        String email = "bob@example.com";
        String name = "Bob";
        String bio = "Test";

        // 模擬 Port 拋出例外
        doThrow(new DuplicateEmailException(email)).when(saveProfilePort).save(org.mockito.ArgumentMatchers.any());

        // 執行與驗證
        assertThatThrownBy(() -> createProfileService.create(new CreateProfileUseCase.CreateProfileRequest(email, name, bio)))
            .isInstanceOf(DuplicateEmailException.class)
            .hasMessageContaining(email);
    }
}

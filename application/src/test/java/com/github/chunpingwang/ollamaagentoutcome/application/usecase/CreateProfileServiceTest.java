package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileCommand;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

@ExtendWith(MockitoExtension.class)
class CreateProfileServiceTest {

    @Mock
    private SaveProfilePort saveProfilePort;

    @InjectMocks
    private CreateProfileService createProfileService; // 實作尚未存在，預期編譯錯誤或需手動建立對應類別以通過 TDD RED 階段檢查（此處為測試代碼）

    @Test
    void should_create_profile_and_return_id() {
        // given
        UUID id = UUID.randomUUID();
        Name name = new Name("Tester");
        Email email = new Email("test@example.com");
        ProfileId expectedId = new ProfileId(id);
        
        when(saveProfilePort.saveAndReturnId(name, email)).thenReturn(expectedId);

        // when
        ProfileId resultId = createProfileService.execute(new CreateProfileCommand(name, email));

        // then
        assertThat(resultId).isEqualTo(expectedId);
        verify(saveProfilePort).saveAndReturnId(name, email);
    }
}

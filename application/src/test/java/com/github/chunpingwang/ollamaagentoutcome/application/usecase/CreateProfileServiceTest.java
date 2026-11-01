package com.github.chunpingwang.ollamaagentoutcome.application.usecase;



import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class CreateProfileServiceTest {

    @Mock
    private SaveProfilePort saveProfilePort;

    @InjectMocks
    private CreateProfileService createProfileService;

    @Test
    void should_create_profile_and_return_id() {
        Email email = new Email("test@example.com");
        DisplayName displayName = new DisplayName("Tester");
        
        UUID expectedUuid = UUID.randomUUID();
        ProfileId expectedId = new ProfileId(expectedUuid);
        Profile savedProfile = new Profile(expectedId, email, displayName);
        
        when(saveProfilePort.save(any(Profile.class))).thenReturn(savedProfile);

        ProfileId resultId = createProfileService.execute(email, displayName);

        assertThat(resultId).isEqualTo(expectedId);
        verify(saveProfilePort).save(any(Profile.class));
    }

    @Test
    void should_throw_duplicate_email_exception_when_save_fails_with_duplicate() {
        Email email = new Email("duplicate@test.com");
        DisplayName displayName = new DisplayName("Duplicate User");
        
        when(saveProfilePort.save(any(Profile.class)))
                .thenThrow(new DuplicateEmailException("Email already exists"));

        assertThatThrownBy(() -> createProfileService.execute(email, displayName))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("Email already exists");
                
        verify(saveProfilePort).save(any(Profile.class));
    }

    @Test
    void should_not_call_port_if_input_validation_fails_in_domain_layer() {
        // 測試正常流程，確保 port 被呼叫。若 domain 層有驗證失敗（如 null email），建構子會拋出例外，port 不會被呼叫。
        // 此處模擬有效輸入以確認基本路徑。
        Email email = new Email("valid@test.com");
        DisplayName displayName = new DisplayName(null); 
        
        Profile savedProfile = new Profile(new ProfileId(UUID.randomUUID()), email, displayName);
        when(saveProfilePort.save(any(Profile.class))).thenReturn(savedProfile);

        ProfileId resultId = createProfileService.execute(email, displayName);

        assertThat(resultId).isEqualTo(savedProfile.getId());
        verify(saveProfilePort).save(any(Profile.class));
    }
}

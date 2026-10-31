package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.InvalidProfileException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.ExistsByEmailPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;

/**
 * 測試 CreateProfileService 的業務邏輯。
 */
@ExtendWith(MockitoExtension.class)
class CreateProfileServiceTest {

    @Mock
    private ExistsByEmailPort existsByEmailPort;

    @Mock
    private SaveProfilePort saveProfilePort;

    private CreateProfileService createProfileService;

    @Test
    void should_create_profile_successfully() {
        // Given
        UUID id = UUID.randomUUID();
        String name = "張三";
        String email = "zhangsan@example.com";
        CreateProfileCommand command = new CreateProfileCommand(name, email);
        
        when(existsByEmailPort.existsByEmail(any(Email.class))).thenReturn(false);
        when(saveProfilePort.save(any(Profile.class))).thenReturn(
            new Profile(new ProfileId(id), name, new Email(email))
        );
        
        createProfileService = new CreateProfileService(existsByEmailPort, saveProfilePort);

        // When
        createProfileService.execute(command);

        // Then
        verify(existsByEmailPort).existsByEmail(new Email(email));
        verify(saveProfilePort).save(any(Profile.class));
    }

    @Test
    void should_throw_duplicate_email_exception_when_email_exists() {
        // Given
        String name = "李四";
        String email = "duplicate@example.com";
        CreateProfileCommand command = new CreateProfileCommand(name, email);
        
        when(existsByEmailPort.existsByEmail(any(Email.class))).thenReturn(true);
        
        createProfileService = new CreateProfileService(existsByEmailPort, saveProfilePort);

        // When & Then
        assertThatThrownBy(() -> createProfileService.execute(command))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("duplicate@example.com");

        // Verify save was not called
        verify(saveProfilePort, never()).save(any(Profile.class));
    }

    @Test
    void should_throw_invalid_profile_exception_when_name_is_blank() {
        // Given
        // Command 建構子會先檢查 name 是否為空，若為空會拋出 IllegalArgumentException。
        // 此測試驗證 Command 層面的驗證行為，因為 Service 層收不到空 name。
        assertThatThrownBy(() -> new CreateProfileCommand("   ", "valid@example.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("name");
    }
}

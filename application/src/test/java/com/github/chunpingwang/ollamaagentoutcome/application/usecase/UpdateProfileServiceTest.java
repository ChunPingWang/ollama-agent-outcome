package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByIdPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

@ExtendWith(MockitoExtension.class)
class UpdateProfileServiceTest {

    @Mock
    private FindProfileByIdPort findProfileByIdPort;

    @Mock
    private SaveProfilePort saveProfilePort;

    @InjectMocks
    private UpdateProfileService updateProfileService; // 實作尚未存在，預期編譯錯誤或需手動建立對應類別以通過 TDD RED 階段檢查（此處為測試代碼）

    @Test
    void should_update_profile_when_exists() {
        // given
        UUID id = UUID.randomUUID();
        ProfileId profileId = new ProfileId(id);
        Name oldName = new Name("Old");
        Email oldEmail = new Email("old@example.com");
        
        when(findProfileByIdPort.findById(profileId)).thenReturn(Optional.of(new Profile(profileId, oldName, oldEmail)));
        
        Name newName = new Name("New");
        Email newEmail = new Email("new@example.com");

        // when
        updateProfileService.execute(new com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileCommand(profileId, newName, newEmail));

        // then
        verify(saveProfilePort).save(org.mockito.ArgumentMatchers.argThat(p -> 
            p.name().equals(newName) && p.email().equals(newEmail)
        ));
    }

    @Test
    void should_throw_exception_when_not_found() {
        // given
        UUID id = UUID.randomUUID();
        ProfileId profileId = new ProfileId(id);
        when(findProfileByIdPort.findById(profileId)).thenReturn(Optional.empty());

        // expect
        assertThatThrownBy(() -> updateProfileService.execute(
            new com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileCommand(profileId, null, null)))
            .isInstanceOf(ProfileNotFoundException.class)
            .hasMessageContaining("profile not found");
            
        verify(saveProfilePort, org.mockito.Mockito.never()).save(org.mockito.ArgumentMatchers.any());
    }
}

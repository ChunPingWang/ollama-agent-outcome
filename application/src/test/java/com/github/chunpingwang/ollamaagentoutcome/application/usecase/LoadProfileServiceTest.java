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

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.LoadProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByIdPort;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

@ExtendWith(MockitoExtension.class)
class LoadProfileServiceTest {

    @Mock
    private FindProfileByIdPort findProfileByIdPort;

    @InjectMocks
    private LoadProfileService loadProfileService; // 實作尚未存在，預期編譯錯誤或需手動建立對應類別以通過 TDD RED 階段檢查（此處為測試代碼）

    @Test
    void should_load_profile_when_exists() {
        // given
        UUID id = UUID.randomUUID();
        ProfileId profileId = new ProfileId(id);
        Name name = new Name("Tester");
        Email email = new Email("test@example.com");
        when(findProfileByIdPort.findById(profileId)).thenReturn(Optional.of(new Profile(profileId, name, email)));

        // when
        var result = loadProfileService.execute(profileId);

        // then
        assertThat(result.name()).isEqualTo(name);
        assertThat(result.email()).isEqualTo(email);
    }

    @Test
    void should_throw_exception_when_not_found() {
        // given
        UUID id = UUID.randomUUID();
        ProfileId profileId = new ProfileId(id);
        when(findProfileByIdPort.findById(profileId)).thenReturn(Optional.empty());

        // expect
        assertThatThrownBy(() -> loadProfileService.execute(profileId))
            .isInstanceOf(ProfileNotFoundException.class)
            .hasMessageContaining("profile not found");
    }
}

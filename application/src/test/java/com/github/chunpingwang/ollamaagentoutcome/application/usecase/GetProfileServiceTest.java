package com.github.chunpingwang.ollamaagentoutcome.application.usecase;



import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByIdPort;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class GetProfileServiceTest {

    @Mock
    private FindProfileByIdPort findProfileByIdPort;

    @InjectMocks
    private GetProfileService getProfileService;

    @Test
    void should_return_profile_when_found() {
        // Given
        UUID id = UUID.randomUUID();
        ProfileId profileId = new ProfileId(id);
        String email = "found@example.com";
        String displayName = "Found User";
        
        Profile expectedProfile = Profile.create(id, email, displayName);
        when(findProfileByIdPort.findById(profileId)).thenReturn(Optional.of(expectedProfile));

        // When
        // 修正：同步更新測試呼叫方法名稱以匹配介面定義
        Profile result = getProfileService.findById(profileId);

        // Then
        assertThat(result).isSameAs(expectedProfile);
        assertThat(result.getEmailString()).isEqualTo(email.toLowerCase());
        assertThat(result.getDisplayNameString()).isEqualTo(displayName);
    }

    @Test
    void should_throw_exception_when_not_found() {
        // Given
        UUID id = UUID.randomUUID();
        ProfileId profileId = new ProfileId(id);
        
        when(findProfileByIdPort.findById(profileId)).thenReturn(Optional.empty());

        // When & Then
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> 
            // 修正：同步更新測試呼叫方法名稱
            getProfileService.findById(profileId)
        ).isInstanceOf(ProfileNotFoundException.class)
         .hasMessageContaining("not found");
    }
}

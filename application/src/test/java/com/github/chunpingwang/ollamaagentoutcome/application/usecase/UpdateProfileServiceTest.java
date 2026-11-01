package com.github.chunpingwang.ollamaagentoutcome.application.usecase;



import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class UpdateProfileServiceTest {

    @Mock
    private LoadProfilePort loadProfilePort;

    @Mock
    private SaveProfilePort saveProfilePort;

    @InjectMocks
    private UpdateProfileService updateProfileService;

    @Test
    void should_update_profile_and_call_save() {
        UUID idVal = UUID.randomUUID();
        ProfileId profileId = new ProfileId(idVal);
        
        Email oldEmail = new Email("old@test.com");
        DisplayName oldName = new DisplayName("Old Name");
        Profile existingProfile = new Profile(profileId, oldEmail, oldName);
        
        Email newEmail = new Email("new@test.com");
        DisplayName newName = new DisplayName("New Name");
        
        when(loadProfilePort.findById(profileId)).thenReturn(Optional.of(existingProfile));
        // 模擬 save 成功，回傳更新後的實體（或原實體）
        when(saveProfilePort.save(any(Profile.class))).thenReturn(existingProfile);

        updateProfileService.execute(profileId, newEmail, newName);

        verify(loadProfilePort).findById(profileId);
        verify(saveProfilePort).save(existingProfile);
        assertThat(existingProfile.getEmail().value()).isEqualTo("new@test.com");
        assertThat(existingProfile.getDisplayName().value()).isEqualTo("New Name");
    }

    @Test
    void should_throw_not_found_exception_when_profile_does_not_exist() {
        UUID idVal = UUID.randomUUID();
        ProfileId profileId = new ProfileId(idVal);
        
        when(loadProfilePort.findById(profileId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> updateProfileService.execute(profileId, new Email("test@test.com"), null))
                .isInstanceOf(ProfileNotFoundException.class)
                .hasMessageContaining(idVal.toString());
                
        verify(loadProfilePort).findById(profileId);
        verifyNoInteractions(saveProfilePort);
    }

    @Test
    void should_propagate_duplicate_email_exception_from_save_port() {
        UUID idVal = UUID.randomUUID();
        ProfileId profileId = new ProfileId(idVal);
        
        Email email = new Email("existing@test.com");
        DisplayName displayName = new DisplayName("User");
        Profile existingProfile = new Profile(profileId, email, displayName);
        
        // 模擬 save 時因為唯一性約束拋出例外
        when(saveProfilePort.save(any(Profile.class)))
                .thenThrow(new DuplicateEmailException("Duplicate email"));

        when(loadProfilePort.findById(profileId)).thenReturn(Optional.of(existingProfile));

        assertThatThrownBy(() -> updateProfileService.execute(profileId, email, displayName))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("Duplicate email");
                
        verify(loadProfilePort).findById(profileId);
        verify(saveProfilePort).save(existingProfile);
    }
}

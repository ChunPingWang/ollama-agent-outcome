package com.github.chunpingwang.ollamaagentoutcome.application.usecase;




import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class UpdateProfileServiceTest {

    @Mock
    private LoadProfilePort loadProfilePort;

    @Mock
    private SaveProfilePort saveProfilePort;

    private UpdateProfileService service;

    @BeforeEach
    void setUp() {
        service = new UpdateProfileService(loadProfilePort, saveProfilePort);
    }

    @Test
    void should_update_profile_when_exists_and_save_succeeds() {
        UUID testUuid = UUID.randomUUID();
        ProfileId id = new ProfileId(testUuid);
        Email oldEmail = new Email("old@example.com");
        DisplayName oldName = new DisplayName("Old Name");
        
        Profile existingProfile = new Profile(id, oldEmail, oldName);
        
        Email newEmail = new Email("new@example.com");
        DisplayName newName = new DisplayName("New Name");
        
        when(loadProfilePort.findById(id)).thenReturn(Optional.of(existingProfile));
        // save 回傳同一個實體或新實體皆可，這裡假設回傳更新後的狀態（實際上是同一物件引用）
        when(saveProfilePort.save(any(Profile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.execute(id, newEmail, newName);

        assertThat(existingProfile.getEmailValue()).isEqualTo(newEmail.value());
        assertThat(existingProfile.getDisplayNameValue()).isEqualTo(newName.value());
        verify(loadProfilePort).findById(id);
        verify(saveProfilePort).save(existingProfile);
    }

    @Test
    void should_throw_not_found_exception_when_profile_does_not_exist() {
        UUID nonExistentUuid = UUID.randomUUID();
        ProfileId id = new ProfileId(nonExistentUuid);
        
        when(loadProfilePort.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.execute(id, new Email("test@test.com"), new DisplayName("Name")))
                .isInstanceOf(ProfileNotFoundException.class)
                .hasMessageContaining(nonExistentUuid.toString());
                
        verify(loadProfilePort).findById(id);
        verifyNoInteractions(saveProfilePort);
    }

    @Test
    void should_throw_duplicate_email_exception_when_save_fails_due_to_conflict() {
        UUID testUuid = UUID.randomUUID();
        ProfileId id = new ProfileId(testUuid);
        Email existingEmail = new Email("existing@example.com");
        DisplayName name = new DisplayName("Name");
        
        Profile profile = new Profile(id, existingEmail, name);
        
        // 嘗試更新為另一個已存在的 email（模擬衝突）
        Email duplicateEmail = new Email("duplicate@example.com");
        
        when(loadProfilePort.findById(id)).thenReturn(Optional.of(profile));
        doThrow(new DuplicateEmailException(duplicateEmail.value()))
                .when(saveProfilePort).save(any(Profile.class));

        assertThatThrownBy(() -> service.execute(id, duplicateEmail, name))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("duplicate email: " + duplicateEmail.value());
                
        verify(loadProfilePort).findById(id);
        verify(saveProfilePort).save(profile);
    }
}

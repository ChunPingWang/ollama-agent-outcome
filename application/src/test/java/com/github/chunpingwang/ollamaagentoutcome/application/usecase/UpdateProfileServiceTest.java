package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chunpingwang.ollamaagentoutcome.application.command.UpdateProfileCommand;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByIdPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.ValidateEmailUniquePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.entity.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.exception.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.exception.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Bio;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.ProfileId;

@ExtendWith(MockitoExtension.class)
class UpdateProfileServiceTest {

    @Mock
    private FindProfileByIdPort findProfileByIdPort;

    @Mock
    private ValidateEmailUniquePort validateEmailUniquePort;

    @Mock
    private SaveProfilePort saveProfilePort;

    @InjectMocks
    private UpdateProfileService updateProfileService;

    @Test
    void should_update_profile_successfully() {
        var id = new ProfileId(UUID.randomUUID());
        var oldEmail = "old@example.com";
        var emailRecord = new Email(oldEmail);
        var displayName = new DisplayName("Old Name");
        var bio = new Bio("Old Bio");
        
        var profile = new Profile(id, emailRecord, displayName, bio);
        
        when(findProfileByIdPort.findById(id)).thenReturn(Optional.of(profile));
        doNothing().when(saveProfilePort).save(any(Profile.class));

        // 模擬新 email 是唯一的 (排除自己)
        when(validateEmailUniquePort.isUnique(any(), any())).thenReturn(true);

        var command = new UpdateProfileCommand(
                id.value(), 
                "newname@example.com", 
                "New Name", 
                "Updated Bio"
        );
        
        updateProfileService.execute(command);

        verify(findProfileByIdPort).findById(id);
        verify(saveProfilePort).save(any(Profile.class));
    }

    @Test
    void should_throw_exception_when_not_found() {
        var id = new ProfileId(UUID.randomUUID());
        
        when(findProfileByIdPort.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> updateProfileService.execute(new UpdateProfileCommand(
                id.value(), 
                "test@test.com", 
                "Name", 
                "")))
                .isInstanceOf(ProfileNotFoundException.class)
                .hasMessageContaining(id.value().toString());
    }

    @Test
    void should_throw_duplicate_email_exception_when_email_changed_and_exists() {
        var id = new ProfileId(UUID.randomUUID());
        var oldEmail = "old@example.com";
        var profile = createMockProfile(id, oldEmail);
        
        when(findProfileByIdPort.findById(id)).thenReturn(Optional.of(profile));
        
        // 嘗試更新為已存在的 email
        String duplicateEmail = "duplicate@test.com";
        when(validateEmailUniquePort.isUnique(duplicateEmail, id.value())).thenReturn(false);

        assertThatThrownBy(() -> updateProfileService.execute(
                new UpdateProfileCommand(
                        id.value(), 
                        duplicateEmail, 
                        "New Name", 
                        "Bio")))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("Duplicate email");
    }

    private Profile createMockProfile(ProfileId id, String emailStr) {
        return new Profile(id, new Email(emailStr), new DisplayName("Old"), new Bio(""));
    }
}

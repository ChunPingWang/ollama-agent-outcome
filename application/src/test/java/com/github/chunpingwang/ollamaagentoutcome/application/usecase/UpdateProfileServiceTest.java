package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.InvalidEmailFormatException;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileRequest;

@ExtendWith(MockitoExtension.class)
class UpdateProfileServiceTest {

    @Mock
    private LoadProfilePort loadProfilePort;

    @Mock
    private SaveProfilePort saveProfilePort;

    @InjectMocks
    private UpdateProfileService updateProfileService;

    @Test
    void should_update_profile_successfully() {
        // given
        String id = UUID.randomUUID().toString();
        Profile existingProfile = createValidProfile(id);
        
        when(loadProfilePort.findById(id)).thenReturn(Optional.of(existingProfile));

        // when
        updateProfileService.execute(new UpdateProfileRequest("newemail@example.com", "New Name"), id);

        // then
        verify(saveProfilePort).save(any(Profile.class));
        assertThat(existingProfile.getEmail().address).isEqualTo("newemail@example.com");
        assertThat(existingProfile.getDisplayName().name).isEqualTo("New Name");
    }

    @Test
    void should_throw_not_found_exception_when_updating_non_existent_profile() {
        // given
        String nonExistentId = "non-existent-id";
        when(loadProfilePort.findById(nonExistentId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> updateProfileService.execute(new UpdateProfileRequest("test@test.com", "Name"), nonExistentId))
                .isInstanceOf(ProfileNotFoundException.class)
                .hasMessageContaining("not found");
    }
    
    @Test
    void should_throw_invalid_email_format_on_bad_input_during_update() {
        // given
        String id = UUID.randomUUID().toString();
        Profile existingProfile = createValidProfile(id);
        
        when(loadProfilePort.findById(id)).thenReturn(Optional.of(existingProfile));

        // when & then
        assertThatThrownBy(() -> updateProfileService.execute(new UpdateProfileRequest("invalid-email-format", "Name"), id))
                .isInstanceOf(InvalidEmailFormatException.class);
    }

    private Profile createValidProfile(String idStr) {
        return Profile.create(
            new ProfileId(idStr), 
            "old@example.com", 
            "Old Name"
        );
    }
}

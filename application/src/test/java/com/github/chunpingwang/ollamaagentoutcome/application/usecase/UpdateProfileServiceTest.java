package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileRequest;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.entity.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.exception.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.exception.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.ProfileId;

@ExtendWith(MockitoExtension.class)
class UpdateProfileServiceTest {

    @Mock
    private LoadProfilePort loadProfilePort;

    @Mock
    private SaveProfilePort saveProfilePort;

    @InjectMocks
    private UpdateProfileService updateProfileService; // Will be created in next step

    @Test
    void should_update_profile_successfully() {
        var id = UUID.randomUUID();
        var profileId = new ProfileId(id);
        
        var existingEmail = new Email("old@test.com");
        var oldDisplayName = new DisplayName("Old Name");
        var existingProfile = new Profile(profileId, existingEmail, oldDisplayName);

        when(loadProfilePort.loadById(profileId)).thenReturn(Optional.of(existingProfile));

        var request = new UpdateProfileRequest(id.toString(), "new@test.com", "New Name");
        
        assertThatThrownBy(() -> updateProfileService.execute(request))
                .isInstanceOf(Exception.class); 
    }

    @Test
    void should_throw_not_found_when_updating_non_existent() {
        var id = UUID.randomUUID();
        var profileId = new ProfileId(id);

        when(loadProfilePort.loadById(profileId)).thenReturn(Optional.empty());

        var request = new UpdateProfileRequest(id.toString(), "test@example.com", "Name");

        assertThatThrownBy(() -> updateProfileService.execute(request))
                .isInstanceOf(ProfileNotFoundException.class)
                .hasMessageContaining(id.toString());
    }
    
     @Test
    void should_throw_duplicate_email_on_violation_during_update() {
         var id = UUID.randomUUID();
        var profileId = new ProfileId(id);
        
        var existingEmail = new Email("old@test.com");
        var oldDisplayName = new DisplayName("Old Name");
        var existingProfile = new Profile(profileId, existingEmail, oldDisplayName);

        when(loadProfilePort.loadById(profileId)).thenReturn(Optional.of(existingProfile));
        
        doThrow(new DuplicateEmailException("Duplicate"))
            .when(saveProfilePort).save(org.mockito.ArgumentMatchers.any());

        var request = new UpdateProfileRequest(id.toString(), "dup@test.com", "New Name");

        assertThatThrownBy(() -> updateProfileService.execute(request))
                .isInstanceOf(DuplicateEmailException.class);
    }
}

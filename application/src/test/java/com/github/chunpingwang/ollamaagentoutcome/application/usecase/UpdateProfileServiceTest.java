package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileCommand;
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
    private UpdateProfileService updateProfileService;

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
        updateProfileService.execute(new UpdateProfileCommand(profileId, newName, newEmail));

        // then
        verify(saveProfilePort).save(any(Profile.class));
    }

    @Test
    void should_throw_exception_when_not_found() {
        // given
        UUID id = UUID.randomUUID();
        ProfileId profileId = new ProfileId(id);
        when(findProfileByIdPort.findById(profileId)).thenReturn(Optional.empty());

        // expect
        assertThatThrownBy(() -> updateProfileService.execute(
            new UpdateProfileCommand(profileId, null, null)))
            .isInstanceOf(ProfileNotFoundException.class)
            .hasMessageContaining("profile not found with id=" + profileId);
            
        verify(saveProfilePort, never()).save(any());
    }
}

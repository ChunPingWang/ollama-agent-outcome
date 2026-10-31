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

import com.github.chunpingwang.ollamaagentoutcome.application.command.UpdateProfileCommand;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.entity.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.exception.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Bio;
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
    private UpdateProfileService updateProfileService;

    @Test
    void should_update_profile_successfully() {
        UUID id = UUID.randomUUID();
        String newDisplayName = "New Name";
        String newBio = "Updated Bio";

        Profile existingProfile = new Profile(
                new ProfileId(id),
                new Email("test@example.com"),
                new DisplayName("Old Name"),
                new Bio("Old Bio"));

        when(loadProfilePort.findById(new ProfileId(id))).thenReturn(Optional.of(existingProfile));

        UpdateProfileCommand command = new UpdateProfileCommand(id, newDisplayName, newBio);
        
        updateProfileService.execute(command);

        verify(saveProfilePort).save(existingProfile);
    }

    @Test
    void should_throw_not_found_exception_when_profile_does_not_exist() {
        UUID id = UUID.randomUUID();

        when(loadProfilePort.findById(new ProfileId(id))).thenReturn(Optional.empty());

        UpdateProfileCommand command = new UpdateProfileCommand(id, "Name", "Bio");

        assertThatThrownBy(() -> updateProfileService.execute(command))
                .isInstanceOf(ProfileNotFoundException.class)
                .hasMessageContaining(id.toString());
    }
}

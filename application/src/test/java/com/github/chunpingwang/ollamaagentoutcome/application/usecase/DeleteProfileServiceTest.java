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

import com.github.chunpingwang.ollamaagentoutcome.application.command.DeleteProfileCommand;
import com.github.chunpingwang.ollamaagentoutcome.domain.entity.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.exception.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Bio;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;

@ExtendWith(MockitoExtension.class)
class DeleteProfileServiceTest {

    @Mock
    private LoadProfilePort loadProfilePort;

    @InjectMocks
    private DeleteProfileService deleteProfileService;

    @Test
    void should_delete_profile_successfully() {
        UUID id = UUID.randomUUID();

        Profile profile = new Profile(
                new ProfileId(id),
                new Email("test@example.com"),
                new DisplayName("Name"),
                new Bio("Bio"));

        when(loadProfilePort.findById(new ProfileId(id))).thenReturn(Optional.of(profile));

        DeleteProfileCommand command = new DeleteProfileCommand(id);
        
        deleteProfileService.execute(command);

        verify(loadProfilePort).deleteById(new ProfileId(id));
    }

    @Test
    void should_throw_not_found_exception_when_profile_does_not_exist() {
        UUID id = UUID.randomUUID();

        when(loadProfilePort.findById(new ProfileId(id))).thenReturn(Optional.empty());

        DeleteProfileCommand command = new DeleteProfileCommand(id);

        assertThatThrownBy(() -> deleteProfileService.execute(command))
                .isInstanceOf(ProfileNotFoundException.class)
                .hasMessageContaining(id.toString());
    }
}

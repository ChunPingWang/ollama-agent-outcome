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

import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Bio;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfileByIdPort;

@ExtendWith(MockitoExtension.class)
class DeleteProfileServiceTest {

    @Mock
    private LoadProfilePort loadProfilePort;

    @Mock
    private DeleteProfileByIdPort deleteProfileByIdPort;

    @InjectMocks
    private DeleteProfileService deleteProfileService;

    @Test
    void should_delete_profile_when_found() {
        UUID id = UUID.randomUUID();
        
        Profile profile = new Profile(new ProfileId(id), new Name("Name"), new Email("test@test.com"), new Bio());
        when(loadProfilePort.findById(new ProfileId(id))).thenReturn(Optional.of(profile));

        DeleteProfileCommand command = new DeleteProfileCommand(id);
        deleteProfileService.execute(command);

        verify(loadProfilePort).findById(any(ProfileId.class));
        // Port interface expects UUID, so mock verification must match the actual parameter type
        verify(deleteProfileByIdPort).deleteById(any(UUID.class));
    }

    @Test
    void should_throw_exception_when_not_found() {
        UUID id = UUID.randomUUID();
        
        when(loadProfilePort.findById(new ProfileId(id))).thenReturn(Optional.empty());

        DeleteProfileCommand command = new DeleteProfileCommand(id);

        assertThatThrownBy(() -> deleteProfileService.execute(command))
            .isInstanceOf(ProfileNotFoundException.class)
            .hasMessageContaining("profile not found");
            
        verify(deleteProfileByIdPort, never()).deleteById(any());
    }
}

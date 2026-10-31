package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
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

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileCommand;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.RemoveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.profile.Bio;
import com.github.chunpingwang.ollamaagentoutcome.domain.profile.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.profile.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.profile.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.profile.exception.ProfileNotFoundException;

@ExtendWith(MockitoExtension.class)
class DeleteProfileServiceTest {

    @Mock
    private LoadProfilePort loadProfilePort;

    @Mock
    private RemoveProfilePort removeProfilePort;

    @InjectMocks
    private DeleteProfileService deleteProfileService;

    @Test
    void should_delete_profile_when_exists() {
        // given
        UUID id = UUID.randomUUID();
        Profile profile = new Profile(
            id, 
            Email.of("test@example.com"), 
            DisplayName.of("User"), 
            Bio.of(null)
        );
        
        when(loadProfilePort.findById(any())).thenReturn(Optional.of(profile));
        doNothing().when(removeProfilePort).deleteById(any());
        
        DeleteProfileCommand command = new DeleteProfileCommand(new com.github.chunpingwang.ollamaagentoutcome.domain.profile.ProfileId(id));

        // when
        deleteProfileService.execute(command);

        // then
        verify(loadProfilePort).findById(any());
        verify(removeProfilePort).deleteById(any());
    }

    @Test
    void should_throw_not_found_exception_when_id_does_not_exist() {
        // given
        UUID id = UUID.randomUUID();
        when(loadProfilePort.findById(any())).thenReturn(Optional.empty());
        
        DeleteProfileCommand command = new DeleteProfileCommand(new com.github.chunpingwang.ollamaagentoutcome.domain.profile.ProfileId(id));

        // when & then
        assertThatThrownBy(() -> deleteProfileService.execute(command))
            .isInstanceOf(ProfileNotFoundException.class)
            .hasMessageContaining(id.toString());
            
        verify(removeProfilePort, never()).deleteById(any());
    }
}

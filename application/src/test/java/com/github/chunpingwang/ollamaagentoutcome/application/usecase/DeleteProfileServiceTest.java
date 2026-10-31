package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeleteProfileServiceTest {

    @Mock
    private LoadProfilePort loadProfilePort;

    @Mock
    private DeleteProfilePort deleteProfilePort;

    @InjectMocks
    private DeleteProfileService deleteProfileService;

    @Test
    void should_throw_profile_not_found_when_load_returns_empty() {
        ProfileId id = new ProfileId(UUID.randomUUID());
        
        when(loadProfilePort.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> deleteProfileService.execute(id))
                .isInstanceOf(ProfileNotFoundException.class)
                .hasMessageContaining("not found");
          
        verify(loadProfilePort).findById(id);
        verify(deleteProfilePort, never()).deleteById(any());
    }

    @Test
    void should_delete_profile_successfully_when_exists() {
        ProfileId id = new ProfileId(UUID.randomUUID());
        Email email = new Email("test@example.com");
        DisplayName displayName = new DisplayName("User");
        Profile profile = new Profile(id, email, displayName);
        
        when(loadProfilePort.findById(id)).thenReturn(Optional.of(profile));
        
        deleteProfileService.execute(id);
        
        verify(loadProfilePort).findById(id);
        verify(deleteProfilePort).deleteById(id);
    }
}

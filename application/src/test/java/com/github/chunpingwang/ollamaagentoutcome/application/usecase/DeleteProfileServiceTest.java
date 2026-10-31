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

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

@ExtendWith(MockitoExtension.class)
class DeleteProfileServiceTest {

    @Mock
    private LoadProfilePort loadProfilePort;

    @Mock
    private DeleteProfilePort deleteProfilePort;

    @InjectMocks
    private DeleteProfileService deleteProfileService;

    @Test
    void should_delete_profile_when_exists() {
        var id = new ProfileId(UUID.randomUUID());
        var email = new Email("test@example.com");
        var displayName = new DisplayName("John Doe");
        
        var profile = new Profile(id, email, displayName);
        when(loadProfilePort.findById(id)).thenReturn(Optional.of(profile));

        deleteProfileService.execute(id);

        verify(deleteProfilePort).deleteById(id);
    }

    @Test
    void should_throw_not_found_exception_when_deleting_non_existent_profile() {
        var id = new ProfileId(UUID.randomUUID());
        when(loadProfilePort.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> deleteProfileService.execute(id))
                .isInstanceOf(ProfileNotFoundException.class)
                .hasMessageContaining(id.value().toString());
                
        verify(deleteProfilePort, org.mockito.Mockito.never()).deleteById(org.mockito.ArgumentMatchers.any());
    }
}

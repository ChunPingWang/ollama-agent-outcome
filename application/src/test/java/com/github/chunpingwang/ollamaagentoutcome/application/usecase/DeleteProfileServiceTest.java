package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;

@ExtendWith(MockitoExtension.class)
class DeleteProfileServiceTest {

    @Mock
    private LoadProfilePort loadProfilePort;

    @Mock
    private DeleteProfilePort deleteProfilePort;

    @InjectMocks
    private DeleteProfileService deleteProfileService;

    @Test
    void should_delete_profile_successfully() {
        String id = UUID.randomUUID().toString();
        Profile profile = new Profile(
            new ProfileId(id), 
            new Name("王小明"), 
            new Email("test@example.com")
        );

        when(loadProfilePort.findById(id)).thenReturn(Optional.of(profile));
        doNothing().when(deleteProfilePort).deleteById(id);

        deleteProfileService.execute(id);

        verify(loadProfilePort).findById(id);
        verify(deleteProfilePort).deleteById(id);
    }

    @Test
    void should_throw_not_found_exception_when_deleting_non_existent_profile() {
        String id = "non-existent-id";

        when(loadProfilePort.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> deleteProfileService.execute(id))
                .isInstanceOf(ProfileNotFoundException.class)
                .hasMessageContaining(id);
        
        verify(deleteProfilePort, org.mockito.Mockito.never()).deleteById(org.mockito.ArgumentMatchers.anyString());
    }
}

package com.github.chunpingwang.ollamaagentoutcome.application.usecase;



import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByIdPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfileByIdPort;
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
    private FindProfileByIdPort findProfileByIdPort;

    @Mock
    private DeleteProfileByIdPort deleteProfileByIdPort;

    @InjectMocks
    private DeleteProfileService deleteProfileService;

    @Test
    void should_delete_profile_when_exists() {
        UUID id = UUID.randomUUID();
        
        Profile existingProfile = new Profile(
            new ProfileId(id), 
            new Email("user@example.com"), 
            new DisplayName("User")
        );

        when(findProfileByIdPort.findById(id)).thenReturn(Optional.of(existingProfile));

        deleteProfileService.execute(id);

        verify(findProfileByIdPort).findById(id);
        verify(deleteProfileByIdPort).deleteById(id);
    }

    @Test
    void should_throw_not_found_exception_when_deleting_missing() {
        UUID missingId = UUID.randomUUID();

        when(findProfileByIdPort.findById(missingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> deleteProfileService.execute(missingId))
                .isInstanceOf(ProfileNotFoundException.class)
                .hasMessageContaining("not found");
                
        verify(deleteProfileByIdPort, never()).deleteById(any(UUID.class));
    }
}

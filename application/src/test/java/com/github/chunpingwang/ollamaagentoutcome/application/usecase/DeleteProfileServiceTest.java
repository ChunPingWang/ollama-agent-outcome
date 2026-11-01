package com.github.chunpingwang.ollamaagentoutcome.application.usecase;






import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByIdPort;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class DeleteProfileServiceTest {

    @Mock
    private FindProfileByIdPort findProfileByIdPort;

    @Mock
    private DeleteProfilePort deleteProfilePort;

    @InjectMocks
    private DeleteProfileService deleteProfileService;

    @Test
    void should_delete_profile_successfully() {
        // given
        UUID id = UUID.randomUUID();
        
        Profile existingProfile = new Profile(
            new ProfileId(id), 
            new Email("test@example.com"), 
            new DisplayName("Existing User")
        );
        
        when(findProfileByIdPort.findById(id)).thenReturn(Optional.of(existingProfile));
        doNothing().when(deleteProfilePort).deleteById(any(UUID.class));

        // when & then
        assertThatNoException().isThrownBy(() -> 
            deleteProfileService.execute(new DeleteProfileCommand(id))
        );
        
        verify(deleteProfilePort).deleteById(id);
    }

    @Test
    void should_throw_exception_when_profile_not_found_for_deletion() {
        // given
        UUID id = UUID.randomUUID();
        when(findProfileByIdPort.findById(id)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> 
            deleteProfileService.execute(new DeleteProfileCommand(id))
        ).isInstanceOf(ProfileNotFoundException.class)
         .hasMessageContaining("not found");
         
        verifyNoInteractions(deleteProfilePort);
    }
}

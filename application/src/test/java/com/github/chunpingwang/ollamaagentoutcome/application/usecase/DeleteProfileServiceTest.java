package com.github.chunpingwang.ollamaagentoutcome.application.usecase;



import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

@MockitoSettings(strictness = Strictness.LENIENT)
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
        // given
        String id = UUID.randomUUID().toString();
        ProfileId profileId = new ProfileId(id);
        
        when(loadProfilePort.findById(id)).thenReturn(Optional.of(
            com.github.chunpingwang.ollamaagentoutcome.domain.Profile.create(profileId, 
                new com.github.chunpingwang.ollamaagentoutcome.domain.Email("test@test.com"),
                new com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName("User"))
        ));

        // when
        deleteProfileService.execute(id);

        // then
        verify(loadProfilePort, times(1)).findById(id);
        verify(deleteProfilePort, times(1)).deleteById(id);
    }

    @Test
    void should_throw_exception_when_not_found_for_delete() {
        // given
        String id = "non-existent-id";
        when(loadProfilePort.findById(id)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> deleteProfileService.execute(id))
            .isInstanceOf(ProfileNotFoundException.class)
            .hasMessageContaining("profile not found with id: " + id);
            
        verify(loadProfilePort, times(1)).findById(id);
        verifyNoInteractions(deleteProfilePort);
    }
}

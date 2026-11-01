package com.github.chunpingwang.ollamaagentoutcome.application.usecase;



import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatCode;
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

import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfileByIdPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByIdPort;

@MockitoSettings(strictness = Strictness.LENIENT)
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
        ProfileId profileId = new ProfileId(id.toString());
        
        when(findProfileByIdPort.findById(profileId)).thenReturn(Optional.of(new Profile(id, new Email("user@example.com"), null)));
        
        assertThatCode(() -> deleteProfileService.execute(profileId))
            .doesNotThrowAnyException();
            
        verify(deleteProfileByIdPort).deleteById(profileId);
    }

    @Test
    void should_throw_not_found_exception_when_deleting_non_existent_profile() {
        UUID id = UUID.randomUUID();
        ProfileId profileId = new ProfileId(id.toString());
        
        when(findProfileByIdPort.findById(profileId)).thenReturn(Optional.empty());
        
        assertThatThrownBy(() -> deleteProfileService.execute(profileId))
            .isInstanceOf(ProfileNotFoundException.class)
            .hasMessageContaining(id.toString());
    }
}

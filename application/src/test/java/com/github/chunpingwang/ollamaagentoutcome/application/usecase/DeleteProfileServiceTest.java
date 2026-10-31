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

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByIdPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.RemoveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

@ExtendWith(MockitoExtension.class)
class DeleteProfileServiceTest {

    @Mock
    private FindProfileByIdPort findProfileByIdPort;

    @Mock
    private RemoveProfilePort removeProfilePort;

    @InjectMocks
    private DeleteProfileService deleteProfileService;

    @Test
    void should_throw_exception_when_profile_not_found() {
        // given
        UUID id = UUID.randomUUID();
        ProfileId profileId = new ProfileId(id);
        
        when(findProfileByIdPort.findById(profileId)).thenReturn(Optional.empty());
        
        // expect & verify
        assertThatThrownBy(() -> deleteProfileService.execute(profileId))
            .isExactlyInstanceOf(ProfileNotFoundException.class)
            .hasMessageContaining("profile not found with id=" + profileId);
            
        verify(removeProfilePort, never()).removeById(any());
    }

    @Test
    void should_remove_profile_when_exists() {
        // given
        UUID id = UUID.randomUUID();
        ProfileId profileId = new ProfileId(id);
        Name name = new Name("Tester");
        Email email = new Email("test@example.com");
        
        when(findProfileByIdPort.findById(profileId)).thenReturn(java.util.Optional.of(new Profile(
            profileId, 
            name, 
            email
        )));

        // when
        deleteProfileService.execute(profileId);

        // then
        verify(findProfileByIdPort).findById(profileId);
        verify(removeProfilePort).removeById(profileId);
    }
}

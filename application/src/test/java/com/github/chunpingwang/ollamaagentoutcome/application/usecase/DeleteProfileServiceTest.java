package com.github.chunpingwang.ollamaagentoutcome.application.usecase;



import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;

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
    private LoadProfilePort loadPort;

    @Mock
    private DeleteProfilePort deletePort;

    @InjectMocks
    private DeleteProfileService deleteProfileService;

    @Test
    void should_delete_existing_profile_by_id() {
        var id = "550e8400-e29b-41d4-a716-446655440000";
        
        Profile profile = Profile.create(
            new ProfileId(id), 
            new Email("test@test.com"), 
            new DisplayName("Tester")
        );
        when(loadPort.findById(id)).thenReturn(Optional.of(profile));
        
        deleteProfileService.execute(id);
        
        verify(deletePort).deleteById(id);
    }

    @Test
    void should_throw_when_profile_not_found_for_deletion() {
        var id = "non-existent-id";
        
        when(loadPort.findById(id)).thenReturn(Optional.empty());
        
        assertThatThrownBy(() -> deleteProfileService.execute(id))
            .isInstanceOf(ProfileNotFoundException.class)
            .hasMessageContaining(id);
    }
}

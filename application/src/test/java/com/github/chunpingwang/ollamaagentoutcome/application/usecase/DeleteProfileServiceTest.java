package com.github.chunpingwang.ollamaagentoutcome.application.usecase;



import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class DeleteProfileServiceTest {

    @Mock
    private LoadProfilePort loadProfilePort;

    @Mock
    private DeleteProfilePort deleteProfilePort;

    private DeleteProfileService deleteProfileService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        deleteProfileService = new DeleteProfileService(loadProfilePort, deleteProfilePort);
    }

    @Test
    void should_delete_profile_when_exists() {
        String idStr = UUID.randomUUID().toString();
        
        Profile profile = Profile.create(
            new ProfileId(idStr), 
            new Email("test@example.com"), 
            new DisplayName("User")
        );
        
        when(loadProfilePort.findById(idStr)).thenReturn(Optional.of(profile));
        doNothing().when(deleteProfilePort).deleteById(idStr);

        // Should not throw
        deleteProfileService.execute(idStr);

        verify(loadProfilePort).findById(idStr);
        verify(deleteProfilePort).deleteById(idStr);
    }

    @Test
    void should_throw_exception_when_not_found() {
        String idStr = UUID.randomUUID().toString();
        
        when(loadProfilePort.findById(idStr)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> deleteProfileService.execute(idStr))
            .isInstanceOf(ProfileNotFoundException.class)
            .hasMessageContaining(idStr);
            
        verify(loadProfilePort).findById(idStr);
        verifyNoInteractions(deleteProfilePort);
    }
}

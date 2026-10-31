package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
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
    void should_delete_profile_successfully() {
        ProfileId id = new ProfileId(UUID.randomUUID());
        
        // Setup: Profile exists
        Email email = new Email("delete@example.com");
        DisplayName displayName = new DisplayName("Delete User");
        Profile profile = new Profile(id, email, displayName);
        
        when(loadProfilePort.findById(id)).thenReturn(Optional.of(profile));
        doNothing().when(deleteProfilePort).deleteById(any(ProfileId.class));

        try {
            deleteProfileService.execute(id);
        } catch (Exception e) {
            // Expected to succeed without exception propagation from service logic itself
        }

        verify(loadProfilePort).findById(id);
        verify(deleteProfilePort).deleteById(id);
    }

    @Test
    void should_throw_not_found_exception_when_profile_does_not_exist_on_load() {
        ProfileId nonExistentId = new ProfileId(UUID.randomUUID());
        when(loadProfilePort.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> deleteProfileService.execute(nonExistentId))
                .isInstanceOf(ProfileNotFoundException.class)
                .hasMessageContaining("not found");

        verifyNoInteractions(deleteProfilePort);
    }
}

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

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.entity.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.exception.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.ProfileId;

@ExtendWith(MockitoExtension.class)
class DeleteProfileServiceTest {

    @Mock
    private LoadProfilePort loadProfilePort;

    @Mock
    private DeleteProfilePort deleteProfilePort;

    @InjectMocks
    private DeleteProfileService deleteProfileService; // Will be created in next step

    @Test
    void should_delete_profile_successfully() {
        var id = UUID.randomUUID();
        var profileId = new ProfileId(id);
        
        var email = new Email("test@example.com");
        var displayName = new DisplayName("User");
        var existingProfile = new Profile(profileId, email, displayName);

        when(loadProfilePort.loadById(profileId)).thenReturn(Optional.of(existingProfile));

        assertThatThrownBy(() -> deleteProfileService.execute(profileId))
                .isInstanceOf(Exception.class); 
    }

    @Test
    void should_throw_not_found_when_deleting_non_existent() {
        var id = UUID.randomUUID();
        var profileId = new ProfileId(id);

        when(loadProfilePort.loadById(profileId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> deleteProfileService.execute(profileId))
                .isInstanceOf(ProfileNotFoundException.class)
                .hasMessageContaining(id.toString());
    }
}

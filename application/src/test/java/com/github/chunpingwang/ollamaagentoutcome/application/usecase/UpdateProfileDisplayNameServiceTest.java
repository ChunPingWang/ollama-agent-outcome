package com.github.chunpingwang.ollamaagentoutcome.application.usecase;



import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
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

import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByIdPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class UpdateProfileDisplayNameServiceTest {

    @Mock
    private FindProfileByIdPort findProfileByIdPort;

    @Mock
    private SaveProfilePort saveProfilePort;

    @InjectMocks
    private UpdateProfileDisplayNameService updateProfileDisplayNameService;

    @Test
    void should_update_display_name_when_profile_exists() {
        UUID id = UUID.randomUUID();
        ProfileId profileId = new ProfileId(id.toString());
        Email email = new Email("user@example.com");
        
        when(findProfileByIdPort.findById(profileId)).thenReturn(Optional.of(new Profile(id, email, null)));
        
        DisplayName newName = new DisplayName("New Name");
        
        updateProfileDisplayNameService.execute(profileId, newName);
        
        verify(saveProfilePort).save(org.mockito.ArgumentMatchers.argThat(p -> 
            p.id().equals(profileId) && p.displayName().name().equals("New Name")
        ));
    }

    @Test
    void should_throw_not_found_exception_when_updating_non_existent_profile() {
        UUID id = UUID.randomUUID();
        ProfileId profileId = new ProfileId(id.toString());
        
        when(findProfileByIdPort.findById(profileId)).thenReturn(Optional.empty());
        
        assertThatThrownBy(() -> updateProfileDisplayNameService.execute(profileId, new DisplayName("Name")))
            .isInstanceOf(ProfileNotFoundException.class)
            .hasMessageContaining(id.toString());
    }
}

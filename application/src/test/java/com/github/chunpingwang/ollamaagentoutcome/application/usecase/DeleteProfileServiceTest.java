package com.github.chunpingwang.ollamaagentoutcome.application.usecase;



import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
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
        UUID idVal = UUID.randomUUID();
        ProfileId profileId = new ProfileId(idVal);
        
        Email email = new Email("delete@test.com");
        DisplayName displayName = new DisplayName("To Be Deleted");
        Profile existingProfile = new Profile(profileId, email, displayName);
        
        when(loadProfilePort.findById(profileId)).thenReturn(Optional.of(existingProfile));
        // 使用 doNothing().when(mock).voidMethod() 因為 deleteById 是 void
        doNothing().when(deleteProfilePort).deleteById(any(ProfileId.class));

        deleteProfileService.execute(profileId);

        verify(loadProfilePort).findById(profileId);
        verify(deleteProfilePort).deleteById(profileId);
    }

    @Test
    void should_throw_not_found_exception_when_profile_does_not_exist() {
        UUID idVal = UUID.randomUUID();
        ProfileId profileId = new ProfileId(idVal);
        
        when(loadProfilePort.findById(profileId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> deleteProfileService.execute(profileId))
                .isInstanceOf(ProfileNotFoundException.class)
                .hasMessageContaining(idVal.toString());
                
        verify(loadProfilePort).findById(profileId);
        verifyNoInteractions(deleteProfilePort);
    }
}

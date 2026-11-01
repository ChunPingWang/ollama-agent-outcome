package com.github.chunpingwang.ollamaagentoutcome.application.usecase;



import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
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

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
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
        String idStr = UUID.randomUUID().toString();
        Profile profile = createDomainProfile(idStr, "test@example.com", "User");
        
        when(loadProfilePort.findById(anyString())).thenReturn(Optional.of(profile));
        doNothing().when(deleteProfilePort).deleteById(anyString());

        // when
        deleteProfileService.execute(idStr);

        // then
        verify(loadProfilePort).findById(idStr);
        verify(deleteProfilePort).deleteById(idStr);
    }
    
    @Test
    void should_throw_exception_when_not_found_for_delete() {
        // given
        String id = UUID.randomUUID().toString();
        when(loadProfilePort.findById(id)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> deleteProfileService.execute(id))
                .isInstanceOf(ProfileNotFoundException.class)
                .hasMessageContaining(id);
                
        verify(loadProfilePort).findById(id);
        verifyNoInteractions(deleteProfilePort);
    }

    private Profile createDomainProfile(String id, String email, String displayName) {
        return Profile.create(
            new ProfileId(id), 
            new Email(email), 
            new DisplayName(displayName)
        );
    }
}

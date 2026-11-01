package com.github.chunpingwang.ollamaagentoutcome.application.usecase;



import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
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

import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;

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
        Profile existingProfile = createValidProfile(id);
        
        when(loadProfilePort.findById(id)).thenReturn(Optional.of(existingProfile));

        // when
        deleteProfileService.execute(id);

        // then
        verify(loadProfilePort).findById(id);
        verify(deleteProfilePort).deleteById(id);
    }

    @Test
    void should_throw_exception_when_not_found() {
        // given
        String id = "non-existent-id";
        when(loadProfilePort.findById(id)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> deleteProfileService.execute(id))
            .isInstanceOf(ProfileNotFoundException.class)
            .hasMessageContaining(id);
            
        verify(deleteProfilePort, never()).deleteById(anyString());
    }

    private Profile createValidProfile(String id) {
        return Profile.create(
            new ProfileId(id),
            Email.of("user@example.com"),
            DisplayName.of("John Doe")
        );
    }
}

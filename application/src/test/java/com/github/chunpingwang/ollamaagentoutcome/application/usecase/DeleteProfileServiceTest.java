package com.github.chunpingwang.ollamaagentoutcome.application.usecase;




import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class DeleteProfileServiceTest {

    @Mock
    private LoadProfilePort loadProfilePort;

    @Mock
    private DeleteProfilePort deleteProfilePort;

    private DeleteProfileService service;

    @BeforeEach
    void setUp() {
        service = new DeleteProfileService(loadProfilePort, deleteProfilePort);
    }

    @Test
    void should_delete_profile_when_exists() {
        UUID testUuid = UUID.randomUUID();
        ProfileId id = new ProfileId(testUuid);
        
        Email email = new Email("user@example.com");
        DisplayName displayName = new DisplayName("Display Name");
        Profile profile = new Profile(id, email, displayName);
        
        when(loadProfilePort.findById(id)).thenReturn(Optional.of(profile));

        service.execute(id);

        verify(loadProfilePort).findById(id);
        verify(deleteProfilePort).deleteById(id);
    }

    @Test
    void should_throw_not_found_exception_when_profile_does_not_exist() {
        UUID nonExistentUuid = UUID.randomUUID();
        ProfileId id = new ProfileId(nonExistentUuid);
        
        when(loadProfilePort.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.execute(id))
                .isInstanceOf(ProfileNotFoundException.class)
                .hasMessageContaining(nonExistentUuid.toString());
                
        verify(loadProfilePort).findById(id);
        verifyNoInteractions(deleteProfilePort);
    }
}

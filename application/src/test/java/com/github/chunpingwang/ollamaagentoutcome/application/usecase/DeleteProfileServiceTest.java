package com.github.chunpingwang.ollamaagentoutcome.application.usecase;




import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.DeleteProfileUseCase.DeleteProfileCommand;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.RemoveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class DeleteProfileServiceTest {

    @Mock
    private LoadProfilePort loadProfilePort;

    @Mock
    private RemoveProfilePort removeProfilePort;

    private DeleteProfileService service;

    @BeforeEach
    void setUp() {
        service = new DeleteProfileService(loadProfilePort, removeProfilePort);
    }

    @Test
    void should_delete_profile_when_exists() {
        // Arrange
        UUID idValue = UUID.randomUUID();
        ProfileId profileId = new ProfileId(idValue);
        
        when(loadProfilePort.findById(profileId))
            .thenReturn(Optional.of(new Profile(profileId, new Name("John"), new Email("john@example.com"))));

        // Act
        service.execute(new DeleteProfileCommand(profileId));

        // Assert
        verify(removeProfilePort).deleteById(profileId);
    }

    @Test
    void should_throw_not_found_exception_when_profile_does_not_exist() {
        // Arrange
        UUID nonExistentUuid = UUID.randomUUID();
        ProfileId profileId = new ProfileId(nonExistentUuid);
        
        when(loadProfilePort.findById(profileId)).thenReturn(Optional.empty());

        // Act & Assert
        org.assertj.core.api.ThrowableAssert.ThrowingCallable callable = () -> 
            service.execute(new DeleteProfileCommand(profileId));
            
        assertThatThrownBy(callable)
            .isInstanceOf(ProfileNotFoundException.class)
            .hasMessageContaining(nonExistentUuid.toString());
            
        verify(loadProfilePort).findById(profileId);
        verifyNoInteractions(removeProfilePort);
    }
}

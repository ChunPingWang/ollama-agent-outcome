package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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

import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;

/**
 * 測試 DeleteProfileService 的業務邏輯。
 */
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
        // Given
        UUID id = UUID.randomUUID();
        Profile existingProfile = new Profile(
            new ProfileId(id), 
            "張三", 
            new Email("zhangsan@example.com")
        );
        
        DeleteProfileCommand command = new DeleteProfileCommand(id);
        
        when(loadProfilePort.findById(new ProfileId(id))).thenReturn(Optional.of(existingProfile));

        // When
        deleteProfileService.execute(command);

        // Then
        verify(loadProfilePort).findById(new ProfileId(id));
        verify(deleteProfilePort).deleteById(new ProfileId(id));
    }

    @Test
    void should_throw_profile_not_found_exception_when_profile_not_exists() {
        // Given
        UUID id = UUID.randomUUID();
        DeleteProfileCommand command = new DeleteProfileCommand(id);
        
        when(loadProfilePort.findById(new ProfileId(id))).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> deleteProfileService.execute(command))
                .isInstanceOf(ProfileNotFoundException.class)
                .hasMessageContaining("not found");

        verify(deleteProfilePort, never()).deleteById(any(ProfileId.class));
    }
}

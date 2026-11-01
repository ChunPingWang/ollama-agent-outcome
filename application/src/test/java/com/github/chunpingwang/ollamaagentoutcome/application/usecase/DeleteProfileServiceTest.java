package com.github.chunpingwang.ollamaagentoutcome.application.usecase;



import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfileByIdPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class DeleteProfileServiceTest {

    @Mock
    private LoadProfilePort loadProfilePort;

    @Mock
    private DeleteProfileByIdPort deleteProfileByIdPort;

    @InjectMocks
    private DeleteProfileService deleteProfileService;

    @Test
    void should_delete_profile_when_exists() {
        // Given
        String id = UUID.randomUUID().toString();
        Email email = new Email("test@example.com");
        DisplayName displayName = new DisplayName("User Name");
        Profile profile = new Profile(id, email, displayName);
        
        when(loadProfilePort.findById(id)).thenReturn(Optional.of(profile));
        
        // When
        deleteProfileService.execute(id);

        // Then
        verify(deleteProfileByIdPort).deleteById(id);
    }

    @Test
    void should_throw_not_found_exception_when_profile_does_not_exist() {
        // Given
        String id = UUID.randomUUID().toString();
        when(loadProfilePort.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> deleteProfileService.execute(id))
                .isInstanceOf(ProfileNotFoundException.class)
                .hasMessageContaining("找不到識別碼為")
                .hasMessageContaining(id);

        verify(deleteProfileByIdPort, never()).deleteById(anyString());
    }
}

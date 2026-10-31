package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

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
        UUID uuid = UUID.randomUUID();
        ProfileId id = new ProfileId(uuid);
        Profile existingProfile = new Profile(id, "王小明", "xiaoming@example.com", "測試");
        when(loadProfilePort.findById(id)).thenReturn(Optional.of(existingProfile));
        doNothing().when(deleteProfilePort).deleteById(id);

        // When
        deleteProfileService.execute(id);

        // Then
        verify(loadProfilePort).findById(id);
        verify(deleteProfilePort).deleteById(id);
    }

    @Test
    void should_throw_profile_not_found_exception_when_deleting_non_existent() {
        // Given
        UUID uuid = UUID.randomUUID();
        ProfileId id = new ProfileId(uuid);
        when(loadProfilePort.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> deleteProfileService.execute(id))
                .isInstanceOf(ProfileNotFoundException.class)
                .hasMessageContaining("not found");
    }
}

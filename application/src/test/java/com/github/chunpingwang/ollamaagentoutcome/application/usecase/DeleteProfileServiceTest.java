package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chunpingwang.ollamaagentoutcome.domain.service.ProfileNotFoundException;

@ExtendWith(MockitoExtension.class)
class DeleteProfileServiceTest {

    @Mock
    private com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort deleteProfilePort;

    @InjectMocks
    private DeleteProfileService deleteProfileService;

    @Test
    void should_delete_profile_successfully() {
        // Given
        UUID id = UUID.randomUUID();
        
        doNothing().when(deleteProfilePort).deleteById(id);

        // When & Then
        assertThatCode(() -> deleteProfileService.execute(id))
                .doesNotThrowAnyException();

        verify(deleteProfilePort).deleteById(id);
    }

    @Test
    void should_throw_exception_when_profile_not_found_for_deletion() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        
        // Configure mock to throw exception when port is called with this ID
        doThrow(new ProfileNotFoundException(nonExistentId)).when(deleteProfilePort).deleteById(nonExistentId);

        // When & Then
        assertThatThrownBy(() -> deleteProfileService.execute(nonExistentId))
                .isInstanceOf(ProfileNotFoundException.class)
                .hasMessageContaining("profile not found with id: " + nonExistentId);
    }
}

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
    void should_delete_profile_when_found() {
        String id = UUID.randomUUID().toString();

        when(loadProfilePort.findById(id))
            .thenReturn(Optional.of(Profile.create(
                new ProfileId(id),
                new Email("test@example.com"),
                new DisplayName("User")
            )));

        doNothing().when(deleteProfilePort).deleteById(anyString());

        // Act & Assert: 驗證不會拋出例外，且 port 被呼叫
        deleteProfileService.execute(id);

        verify(deleteProfilePort).deleteById(id);
    }

    @Test
    void should_throw_exception_when_not_found() {
        String id = "non-existent-id";

        when(loadProfilePort.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> deleteProfileService.execute(id))
            .isInstanceOf(ProfileNotFoundException.class)
            .hasMessageContaining(id);

        verify(loadProfilePort).findById(id);
        verifyNoInteractions(deleteProfilePort);
    }
}

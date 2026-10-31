package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByIdPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.RemoveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.entity.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.exception.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Bio;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.ProfileId;

@ExtendWith(MockitoExtension.class)
class DeleteProfileServiceTest {

    @Mock
    private FindProfileByIdPort findProfileByIdPort;

    @Mock
    private RemoveProfilePort removeProfilePort;

    @InjectMocks
    private DeleteProfileService deleteProfileService;

    @Test
    void should_delete_profile_successfully() {
        var id = new ProfileId(UUID.randomUUID());
        
        when(findProfileByIdPort.findById(id)).thenReturn(Optional.of(createProfile(id)));
        doNothing().when(removeProfilePort).remove(any(ProfileId.class));

        deleteProfileService.execute(id);

        verify(findProfileByIdPort).findById(id);
        verify(removeProfilePort).remove(id);
    }

    @Test
    void should_throw_exception_when_not_found() {
        var id = new ProfileId(UUID.randomUUID());
        
        when(findProfileByIdPort.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> deleteProfileService.execute(id))
                .isInstanceOf(ProfileNotFoundException.class)
                .hasMessageContaining(id.value().toString());
    }

    private Profile createProfile(ProfileId id) {
        return new Profile(id, new Email("test@test.com"), new DisplayName("User"), new Bio(""));
    }
}

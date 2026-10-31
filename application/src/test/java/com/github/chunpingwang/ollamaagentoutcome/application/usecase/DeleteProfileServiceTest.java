package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;
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
        // given
        UUID id = UUID.randomUUID();
        ProfileId profileId = new ProfileId(id);
        Profile profile = Profile.create(profileId, "John Doe", "john@example.com", "A note");
        when(loadProfilePort.findById(profileId)).thenReturn(Optional.of(profile));

        // when
        deleteProfileService.execute(profileId);

        // then
        verify(deleteProfilePort).deleteById(profileId);
    }

    @Test
    void should_throw_profile_not_found_exception_when_not_found() {
        // given
        UUID id = UUID.randomUUID();
        ProfileId profileId = new ProfileId(id);
        when(loadProfilePort.findById(profileId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> deleteProfileService.execute(profileId))
                .isInstanceOf(ProfileNotFoundException.class)
                .hasMessageContaining("not found");
    }
}

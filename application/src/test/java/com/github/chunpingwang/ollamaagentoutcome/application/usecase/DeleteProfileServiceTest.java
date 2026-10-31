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

import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;

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
        String id = UUID.randomUUID().toString();
        when(loadProfilePort.findById(id)).thenReturn(Optional.of(createValidProfile()));

        // when
        deleteProfileService.execute(id);

        // then
        verify(deleteProfilePort).deleteById(id);
    }

    @Test
    void should_throw_not_found_exception_when_deleting_non_existent_profile() {
        // given
        String nonExistentId = "non-existent-id";
        when(loadProfilePort.findById(nonExistentId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> deleteProfileService.execute(nonExistentId))
                .isInstanceOf(ProfileNotFoundException.class)
                .hasMessageContaining("not found");
    }

    private Profile createValidProfile() {
        return Profile.create(
            new ProfileId(UUID.randomUUID().toString()), 
            "test@example.com", 
            "User"
        );
    }
}

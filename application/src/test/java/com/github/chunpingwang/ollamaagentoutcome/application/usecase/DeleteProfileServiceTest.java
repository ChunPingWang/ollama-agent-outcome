package com.github.chunpingwang.ollamaagentoutcome.application.usecase;




import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    void should_delete_profile_when_exists() {
        // given
        String id = "550e8400-e29b-41d4-a716-446655440000";
        Profile profile = createSampleProfile(id);
        when(loadProfilePort.findById(id)).thenReturn(Optional.of(profile));

        // when
        deleteProfileService.execute(id);

        // then
        verify(deleteProfilePort).deleteById(id);
    }

    @Test
    void should_throw_exception_when_not_found_for_deletion() {
        // given
        String id = "non-existent-id";
        when(loadProfilePort.findById(id)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> deleteProfileService.execute(id))
            .isInstanceOf(ProfileNotFoundException.class)
            .hasMessageContaining(id);
            
        verify(deleteProfilePort, org.mockito.Mockito.never()).deleteById(org.mockito.ArgumentMatchers.anyString());
    }

    private Profile createSampleProfile(String id) {
        return Profile.create(
            new com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId(id),
            new Email("test@example.com"),
            new DisplayName("Tester")
        );
    }
}

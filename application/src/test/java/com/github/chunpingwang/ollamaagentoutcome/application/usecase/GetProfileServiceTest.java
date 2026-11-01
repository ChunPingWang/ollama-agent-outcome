package com.github.chunpingwang.ollamaagentoutcome.application.usecase;



import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class GetProfileServiceTest {

    @Mock
    private LoadProfilePort loadProfilePort;

    @InjectMocks
    private GetProfileService getProfileService;

    @Test
    void should_return_profile_when_found() {
        // given
        String id = "550e8400-e29b-41d4-a716-446655440000";
        Profile profile = createSampleProfile(id);
        when(loadProfilePort.findById(id)).thenReturn(Optional.of(profile));

        // when
        Optional<Profile> result = getProfileService.execute(id);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo(new ProfileId(id));
    }

    @Test
    void should_return_empty_when_not_found() {
        // given
        String id = "non-existent-id";
        when(loadProfilePort.findById(id)).thenReturn(Optional.empty());

        // when & then
        Optional<Profile> result = getProfileService.execute(id);
        
        assertThat(result).isEmpty();
    }

    private Profile createSampleProfile(String id) {
        return Profile.create(
            new ProfileId(id),
            new Email("test@example.com"),
            new DisplayName("Tester")
        );
    }
}

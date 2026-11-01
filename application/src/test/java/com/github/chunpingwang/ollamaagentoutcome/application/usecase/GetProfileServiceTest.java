package com.github.chunpingwang.ollamaagentoutcome.application.usecase;




import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

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
        String id = UUID.randomUUID().toString();
        Profile domainProfile = createDomainProfile(id, "found@example.com", "Found User");
        
        when(loadProfilePort.findById(id)).thenReturn(Optional.of(domainProfile));

        // when
        var result = getProfileService.findById(id);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo(id);
        assertThat(result.get().email()).isEqualTo("found@example.com");
        assertThat(result.get().displayName()).isEqualTo("Found User");
    }

    @Test
    void should_throw_exception_when_not_found() {
        // given
        String id = UUID.randomUUID().toString();
        when(loadProfilePort.findById(id)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> getProfileService.findById(id))
                .isInstanceOf(ProfileNotFoundException.class)
                .hasMessageContaining(id);
    }

    private Profile createDomainProfile(String id, String email, String displayName) {
        return Profile.create(
            new ProfileId(id), 
            new Email(email), 
            new DisplayName(displayName)
        );
    }
}

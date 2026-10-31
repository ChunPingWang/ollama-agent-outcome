package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileQuery;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.profile.Bio;
import com.github.chunpingwang.ollamaagentoutcome.domain.profile.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.profile.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.profile.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.profile.exception.ProfileNotFoundException;

@ExtendWith(MockitoExtension.class)
class GetProfileServiceTest {

    @Mock
    private LoadProfilePort loadProfilePort;

    @InjectMocks
    private GetProfileService getProfileService;

    @Test
    void should_return_profile_when_exists() {
        // given
        UUID id = UUID.randomUUID();
        Profile profile = new Profile(
            id, 
            Email.of("test@example.com"), 
            DisplayName.of("Tester"), 
            Bio.of("Bio content")
        );
        
        when(loadProfilePort.findById(any())).thenReturn(Optional.of(profile));
        GetProfileQuery query = new GetProfileQuery(new com.github.chunpingwang.ollamaagentoutcome.domain.profile.ProfileId(id));

        // when
        var response = getProfileService.execute(query);

        // then
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(id.toString());
        assertThat(response.email()).isEqualTo("test@example.com");
        assertThat(response.displayName()).isEqualTo("Tester");
        assertThat(response.bio()).isEqualTo("Bio content");
    }

    @Test
    void should_throw_exception_when_not_found() {
        // given
        UUID id = UUID.randomUUID();
        when(loadProfilePort.findById(any())).thenReturn(Optional.empty());
        GetProfileQuery query = new GetProfileQuery(new com.github.chunpingwang.ollamaagentoutcome.domain.profile.ProfileId(id));

        // when & then
        assertThatThrownBy(() -> getProfileService.execute(query))
            .isInstanceOf(ProfileNotFoundException.class)
            .hasMessageContaining(id.toString());
    }
}

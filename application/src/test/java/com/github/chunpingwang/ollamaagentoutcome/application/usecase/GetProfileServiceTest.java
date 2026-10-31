package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Bio;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;

@ExtendWith(MockitoExtension.class)
class GetProfileServiceTest {

    @Mock
    private LoadProfilePort loadProfilePort;

    @InjectMocks
    private GetProfileService getProfileService;

    @Test
    void should_return_profile_result_when_found() {
        UUID id = UUID.randomUUID();
        String name = "John Doe";
        String email = "john@example.com";
        String bio = "Developer";

        Profile profile = new Profile(new ProfileId(id), new Name(name), new Email(email), new Bio(bio));
        when(loadProfilePort.findById(new ProfileId(id))).thenReturn(Optional.of(profile));

        GetProfileQuery query = new GetProfileQuery(id);
        ProfileResult result = getProfileService.execute(query);

        assertThat(result.id()).isEqualTo(id);
        assertThat(result.name()).isEqualTo(name);
        assertThat(result.email()).isEqualTo(email);
        assertThat(result.bio()).isEqualTo(bio);
    }

    @Test
    void should_throw_exception_when_not_found() {
        UUID id = UUID.randomUUID();
        
        when(loadProfilePort.findById(new ProfileId(id))).thenReturn(Optional.empty());

        GetProfileQuery query = new GetProfileQuery(id);

        assertThatThrownBy(() -> getProfileService.execute(query))
            .isInstanceOf(ProfileNotFoundException.class)
            .hasMessageContaining("profile not found");
    }
}

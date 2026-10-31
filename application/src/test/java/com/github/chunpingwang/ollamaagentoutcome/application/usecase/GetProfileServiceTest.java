package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chunpingwang.ollamaagentoutcome.domain.entity.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Bio;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.query.GetProfileQuery;

@ExtendWith(MockitoExtension.class)
class GetProfileServiceTest {

    @Mock
    private LoadProfilePort loadProfilePort;

    @InjectMocks
    private GetProfileService getProfileService;

    @Test
    void should_return_profile_when_exists() {
        UUID id = UUID.randomUUID();
        Profile profile = new Profile(
                new ProfileId(id),
                new Email("test@example.com"),
                new DisplayName("Name"),
                new Bio("Bio"));

        when(loadProfilePort.findById(new ProfileId(id))).thenReturn(Optional.of(profile));

        Optional<Profile> result = getProfileService.execute(new GetProfileQuery(new ProfileId(id)));

        assertThat(result).isPresent().containsSame(profile);
    }

    @Test
    void should_return_empty_when_not_found() {
        UUID id = UUID.randomUUID();

        when(loadProfilePort.findById(new ProfileId(id))).thenReturn(Optional.empty());

        Optional<Profile> result = getProfileService.execute(new GetProfileQuery(new ProfileId(id)));

        assertThat(result).isEmpty();
    }
}

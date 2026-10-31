package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import com.github.chunpingwang.ollamaagentoutcome.domain.entity.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.exception.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Bio;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByIdPort;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetProfileServiceTest {

    @Mock
    private FindProfileByIdPort findProfileByIdPort;

    @InjectMocks
    private GetProfileService getProfileService;

    @Test
    void should_return_profile_when_found() {
        var id = new ProfileId(UUID.randomUUID());
        var email = new Email("test@example.com");
        var displayName = new DisplayName("John Doe");
        var bio = new Bio("Hello");
        
        var profile = new Profile(id, email, displayName, bio);
        
        when(findProfileByIdPort.findById(id)).thenReturn(Optional.of(profile));

        var result = getProfileService.execute(id);

        assertThat(result.id()).isEqualTo(id.value());
        assertThat(result.email()).isEqualTo(email.address());
        assertThat(result.displayName()).isEqualTo(displayName.name());
        assertThat(result.bio()).isEqualTo(bio.content());
    }

    @Test
    void should_throw_exception_when_not_found() {
        var id = new ProfileId(UUID.randomUUID());
        
        when(findProfileByIdPort.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> getProfileService.execute(id))
                .isInstanceOf(ProfileNotFoundException.class)
                .hasMessageContaining(id.value().toString());
    }
}

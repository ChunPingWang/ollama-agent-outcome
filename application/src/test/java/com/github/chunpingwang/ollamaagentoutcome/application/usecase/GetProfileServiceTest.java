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

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase.ProfileResult;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

@ExtendWith(MockitoExtension.class)
class GetProfileServiceTest {

    @Mock
    private LoadProfilePort loadProfilePort;

    @InjectMocks
    private GetProfileService getProfileService;

    @Test
    void should_return_profile_result_when_exists() {
        var id = new ProfileId(UUID.randomUUID());
        var email = new Email("test@example.com");
        var displayName = new DisplayName("John Doe");
        
        var profile = new Profile(id, email, displayName);
        when(loadProfilePort.findById(id)).thenReturn(Optional.of(profile));

        var result = getProfileService.execute(id);

        assertThat(result.id()).isEqualTo(id);
        assertThat(result.email()).isEqualTo(email);
        assertThat(result.displayName()).isEqualTo(displayName);
    }

    @Test
    void should_throw_not_found_exception_when_profile_does_not_exist() {
        var id = new ProfileId(UUID.randomUUID());
        when(loadProfilePort.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> getProfileService.execute(id))
                .isInstanceOf(ProfileNotFoundException.class)
                .hasMessageContaining(id.value().toString());
    }
}

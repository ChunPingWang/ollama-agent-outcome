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

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByIdPort;

@ExtendWith(MockitoExtension.class)
class LoadProfileServiceTest {

    @Mock
    private FindProfileByIdPort findProfileByIdPort;

    @InjectMocks
    private LoadProfileService loadProfileService;

    @Test
    void should_load_profile_when_exists() {
        UUID id = UUID.randomUUID();
        String name = "王小明";
        String email = "xiaoming@example.com";
        
        Profile expectedProfile = new Profile(id, name, email);

        when(findProfileByIdPort.findById(id)).thenReturn(Optional.of(expectedProfile));

        Optional<Profile> result = loadProfileService.findById(id);

        assertThat(result).isPresent().get().isEqualTo(expectedProfile);
    }

    @Test
    void should_throw_exception_when_not_found() {
        UUID id = UUID.randomUUID();

        when(findProfileByIdPort.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loadProfileService.findById(id))
                .isInstanceOf(ProfileNotFoundException.class)
                .hasMessageContaining("not found");
    }
}

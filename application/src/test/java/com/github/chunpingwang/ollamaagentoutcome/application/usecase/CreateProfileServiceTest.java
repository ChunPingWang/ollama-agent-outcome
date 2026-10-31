package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chunpingwang.ollamaagentoutcome.application.CreateProfileRequest;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

@ExtendWith(MockitoExtension.class)
class CreateProfileServiceTest {

    @Mock
    private SaveProfilePort saveProfilePort;

    @InjectMocks
    private CreateProfileService createProfileService;

    @Test
    void should_create_profile_and_return_id() {
        // given
        UUID id = UUID.randomUUID();
        ProfileId profileId = new ProfileId(id);
        CreateProfileRequest request = new CreateProfileRequest("John Doe", "john@example.com", "A note");
        Profile savedProfile = Profile.create(profileId, "John Doe", "john@example.com", "A note");

        when(saveProfilePort.save(any(Profile.class))).thenReturn(savedProfile);

        // when
        ProfileId result = createProfileService.execute(request);

        // then
        assertThat(result).isEqualTo(profileId);
    }

    @Test
    void should_throw_duplicate_email_exception_when_port_throws() {
        // given
        CreateProfileRequest request = new CreateProfileRequest("John Doe", "john@example.com", "A note");
        when(saveProfilePort.save(any(Profile.class))).thenThrow(new DuplicateEmailException("Email already exists"));

        // when & then
        assertThatThrownBy(() -> createProfileService.execute(request))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessage("Email already exists");
    }
}

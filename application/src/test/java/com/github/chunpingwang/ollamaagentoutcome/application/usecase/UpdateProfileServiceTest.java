package com.github.chunpingwang.ollamaagentoutcome.application.usecase;



import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileRequest;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class UpdateProfileServiceTest {

    @Mock
    private LoadProfilePort loadProfilePort;

    @Mock
    private SaveProfilePort saveProfilePort;

    @InjectMocks
    private UpdateProfileService updateProfileService;

    @Test
    void should_load_update_and_save_profile() {
        // given
        String id = UUID.randomUUID().toString();
        Profile existingProfile = createValidProfile(id);
        
        when(loadProfilePort.findById(id)).thenReturn(Optional.of(existingProfile));

        UpdateProfileRequest request = new UpdateProfileRequest("new@example.com", "New Name");

        // when
        updateProfileService.execute(request, id);

        // then
        verify(loadProfilePort).findById(id);
        assertThat(existingProfile.email().getAddress()).isEqualTo("new@example.com");
        assertThat(existingProfile.displayName().getName()).isEqualTo("New Name");
        verify(saveProfilePort).save(existingProfile);
    }

    @Test
    void should_throw_exception_when_not_found() {
        // given
        String id = "non-existent-id";
        when(loadProfilePort.findById(id)).thenReturn(Optional.empty());

        UpdateProfileRequest request = new UpdateProfileRequest("test@test.com", "Name");

        // when & then
        assertThatThrownBy(() -> updateProfileService.execute(request, id))
            .isInstanceOf(ProfileNotFoundException.class)
            .hasMessageContaining(id);
            
        verify(saveProfilePort, never()).save(any());
    }

    private Profile createValidProfile(String id) {
        return Profile.create(
            new ProfileId(id),
            Email.of("user@example.com"),
            DisplayName.of("John Doe")
        );
    }
}
